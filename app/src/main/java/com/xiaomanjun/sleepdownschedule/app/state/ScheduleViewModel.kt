package com.xiaomanjun.sleepdownschedule.app.state

import com.xiaomanjun.sleepdownschedule.*

import com.xiaomanjun.sleepdownschedule.feature.schedule.*

import com.xiaomanjun.sleepdownschedule.core.wallpaper.*
import com.xiaomanjun.sleepdownschedule.feature.reminder.NotificationScheduler


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(
    private val app: Application,
    private val repository: ScheduleRepository
) : AndroidViewModel(app) {
    val state: StateFlow<AppState> = repository.state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppState()
    )
    val allSchedulesState: StateFlow<AppState> = repository.allSchedulesState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppState()
    )
    val themeConfig: StateFlow<ScheduleConfigEntity> = state
        .map { it.config }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultConfig())
    val snackbar = MutableStateFlow<String?>(null)
    // Each schedule keeps one complete pending personalization snapshot. The signal is conflated,
    // while the map preserves the latest snapshot for every schedule, so rapid edits never replay
    // obsolete states or drop another field changed between two database writes.
    private val personalizationSaveSignal = Channel<Unit>(Channel.CONFLATED)
    private val personalizationSaveLock = Any()
    private val pendingPersonalizationSnapshots = linkedMapOf<Int, ScheduleConfigEntity>()
    private val refreshCoordinator = ScheduleRefreshCoordinator(
        scope = viewModelScope,
        refresh = ::refreshScheduleSurfaces,
        onFailure = { error ->
            Log.w("ScheduleViewModel", "Schedule surface refresh failed", error)
            snackbar.value = error.message ?: "系统课表界面刷新失败，请稍后重试"
        }
    )

    init {
        viewModelScope.launch {
            repository.ensureDefaults()
            refreshCoordinator.refreshNow()
        }
        viewModelScope.launch {
            for (ignored in personalizationSaveSignal) {
                while (true) {
                    val batch = synchronized(personalizationSaveLock) {
                        if (pendingPersonalizationSnapshots.isEmpty()) {
                            emptyList()
                        } else {
                            pendingPersonalizationSnapshots.values.toList().also {
                                pendingPersonalizationSnapshots.clear()
                            }
                        }
                    }
                    if (batch.isEmpty()) break
                    batch.forEach { snapshot ->
                        try {
                            if (repository.savePersonalizationSnapshot(snapshot)) {
                                cleanupScheduleWallpaperFiles()
                            }
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (error: Throwable) {
                            Log.w("ScheduleViewModel", "Personalization save failed", error)
                            snackbar.value = error.message ?: "个性化设置保存失败，请重试"
                        }
                    }
                }
            }
        }
    }

    fun addCourse(course: CourseEntity) = launchCourseMutation {
        repository.addCourse(course)
    }

    fun addCourses(courses: List<CourseEntity>) = launchCourseMutation {
        repository.addCourses(courses)
    }

    fun updateCourse(course: CourseEntity) = launchCourseMutation("课程已更新") {
        repository.updateCourse(course)
    }

    fun replaceCourseGroup(originals: List<CourseEntity>, replacements: List<CourseEntity>) =
        launchCourseMutation("课程已更新") {
            repository.replaceCourseGroup(originals, replacements)
        }

    fun replaceCourseGroupAndThen(
        originals: List<CourseEntity>,
        replacements: List<CourseEntity>,
        onSuccess: () -> Unit
    ) = launchCourseMutation("课程已更新", onSuccess) {
        repository.replaceCourseGroup(originals, replacements)
    }

    fun updateCourseSingleWeek(original: CourseEntity, edited: CourseEntity, targetWeek: Int) =
        launchCourseMutation {
        repository.updateCourseSingleWeek(original, edited, targetWeek)
    }

    fun updateRelatedCourses(original: CourseEntity, edited: CourseEntity) =
        launchCourseMutation("课程已更新") {
        repository.updateRelatedCourses(original, edited)
    }

    fun deleteCourse(course: CourseEntity) = launchCourseMutation("课程已删除") {
        repository.deleteCourse(course)
    }

    fun deleteCourses(courses: List<CourseEntity>) = launchCourseMutation("课程已删除") {
        repository.deleteCourses(courses)
    }

    fun deleteCoursesAndThen(courses: List<CourseEntity>, onSuccess: () -> Unit) =
        launchCourseMutation("课程已删除", onSuccess) {
            repository.deleteCourses(courses)
        }

    fun deleteCoursesSingleWeek(courses: List<CourseEntity>, targetWeek: Int) =
        launchCourseMutation("课程已删除") {
            repository.deleteCoursesSingleWeek(courses, targetWeek)
        }

    fun deleteCourseSingleWeek(course: CourseEntity, targetWeek: Int) =
        launchCourseMutation("课程已删除") {
        repository.deleteCourseSingleWeek(course, targetWeek)
    }

    private fun launchCourseMutation(
        successMessage: String? = null,
        onSuccess: (() -> Unit)? = null,
        mutation: suspend () -> Unit
    ) = viewModelScope.launch {
        try {
            mutation()
            refreshCoordinator.request()
            successMessage?.let { snackbar.value = it }
            onSuccess?.invoke()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Throwable) {
            Log.w("ScheduleViewModel", "Course mutation rejected", error)
            snackbar.value = error.message ?: "课程操作失败，请重试"
        }
    }

    fun importDraft(
        draft: ImportDraft,
        createNewSchedule: Boolean = false,
        onDone: (Int) -> Unit
    ) = viewModelScope.launch {
        val scheduleId = repository.importDraft(draft, createNewSchedule)
        refreshCoordinator.request()
        snackbar.value = if (createNewSchedule) "已导入到新课表" else "课程表已导入"
        onDone(scheduleId)
    }

    fun saveConfig(config: ScheduleConfigEntity, periods: List<PeriodEntity>) = viewModelScope.launch {
        repository.saveConfig(config, periods)
        refreshCoordinator.request()
    }

    fun saveConfigForSchedule(
        scheduleId: Int,
        config: ScheduleConfigEntity,
        periods: List<PeriodEntity>,
        finish: (() -> Unit)? = null
    ) = viewModelScope.launch {
        repository.saveConfigForSchedule(scheduleId, config, periods)
        refreshCoordinator.request()
        finish?.invoke()
    }

    fun savePersonalization(config: ScheduleConfigEntity) {
        synchronized(personalizationSaveLock) {
            pendingPersonalizationSnapshots[config.id] = config
        }
        personalizationSaveSignal.trySend(Unit)
    }

    fun savePersonalizationAndFinish(config: ScheduleConfigEntity, finish: () -> Unit) =
        viewModelScope.launch {
            try {
                if (repository.savePersonalizationSnapshot(config)) {
                    cleanupScheduleWallpaperFiles()
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Throwable) {
                Log.w("ScheduleViewModel", "Personalization exit save failed", error)
                snackbar.value = error.message ?: "个性化设置保存失败，请重试"
            }
            finish()
        }

    fun saveGeneralSettings(config: ScheduleConfigEntity) {
        (app as CourseScheduleApp).enqueueGeneralSettingsSave(config)
    }

    fun saveHomeChromeBlurScale(value: Float) {
        (app as CourseScheduleApp).enqueueHomeChromeBlurScaleSave(value)
    }

    fun saveNotificationSettings(config: ScheduleConfigEntity) {
        (app as CourseScheduleApp).enqueueNotificationSettingsSave(config)
    }

    fun saveGlobalSettingsAndFinish(
        config: ScheduleConfigEntity,
        onFinished: (Boolean) -> Unit
    ) =
        viewModelScope.launch {
            try {
                repository.saveGlobalSettings(config)
                refreshCoordinator.request()
                onFinished(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Throwable) {
                Log.w("ScheduleViewModel", "Global settings exit save failed", error)
                snackbar.value = error.message ?: "设置保存失败，请重试"
                onFinished(false)
            }
        }

    fun createSchedule(
        name: String = "\u65B0\u8BFE\u8868",
        activate: Boolean = true,
        onCreated: ((Int) -> Unit)? = null
    ) = viewModelScope.launch {
        Log.d("ScheduleManager", "viewModel.createSchedule name=$name")
        val scheduleId = repository.createSchedule(name)
        Log.d("ScheduleManager", "repository.createSchedule created id=$scheduleId")
        if (activate) repository.activateSchedule(scheduleId)
        refreshCoordinator.request()
        onCreated?.invoke(scheduleId)
    }

    fun activateSchedule(scheduleId: Int, finish: (() -> Unit)? = null) = viewModelScope.launch {
        Log.d("ScheduleManager", "viewModel.activateSchedule id=$scheduleId")
        repository.activateSchedule(scheduleId)
        refreshCoordinator.request()
        finish?.invoke()
    }

    fun renameSchedule(scheduleId: Int, name: String) = viewModelScope.launch {
        Log.d("ScheduleManager", "viewModel.renameSchedule id=$scheduleId name=$name")
        repository.renameSchedule(scheduleId, name)
    }

    fun deleteSchedule(scheduleId: Int) = viewModelScope.launch {
        Log.d("ScheduleManager", "viewModel.deleteSchedule id=$scheduleId")
        repository.deleteSchedule(scheduleId)
        cleanupScheduleWallpaperFiles()
        refreshCoordinator.request()
    }

    fun clearSnackbar() {
        snackbar.value = null
    }

    fun previewLiveUpdate() = viewModelScope.launch {
        val snapshot = repository.activeSnapshot()
        NotificationScheduler.showLiveUpdatePreview(app, snapshot.config)
        val minutes = snapshot.config.notificationLeadMinutes.coerceIn(1, 30)
        snackbar.value = "已启动测试实时活动（${minutes}分钟倒计时）"
    }

    fun refreshNotifications() {
        refreshCoordinator.request()
    }
    private suspend fun refreshScheduleSurfaces() = withContext(Dispatchers.IO) {
        val snapshot = repository.activeSnapshot()
        NotificationScheduler.refreshToday(app, snapshot.courses, snapshot.config, snapshot.periods)
        TodayCoursesWidgetProvider.refreshAll(app)
    }

    private suspend fun cleanupScheduleWallpaperFiles() = withContext(Dispatchers.IO) {
        cleanupUnreferencedScheduleWallpapers(app, repository.referencedWallpaperUris())
    }
}

class ScheduleViewModelFactory(
    private val app: Application,
    private val repository: ScheduleRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
        ScheduleViewModel(app, repository) as T
}
