package com.xiaomanjun.sleepdownschedule.feature.reminder

import com.xiaomanjun.sleepdownschedule.*

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object NotificationScheduler {
    private const val CHANNEL_ID = "course_reminders"
    private const val PREFS = "course_alarm_prefs"
    private const val KEY_REQUEST_CODES = "request_codes"
    private const val KEY_SCHEDULE_SIGNATURE = "schedule_signature"
    // 实时活动功能已下线。以下常量只用于清除旧版本遗留的通知与服务状态。
    private const val LEGACY_LIVE_UPDATE_ID = 20260522
    private const val LEGACY_LIVE_UPDATE_PREFS = "live_update_service_state"
    private const val TEST_NOTIFICATION_ID = 20260829

    inline fun withShortWakeLock(context: Context, tagSuffix: String, block: () -> Unit) {
        val powerManager = context.getSystemService(PowerManager::class.java)
        val wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SleepDown:$tagSuffix")
        runCatching { wakeLock?.acquire(5_000L) }
        try {
            block()
        } finally {
            if (wakeLock?.isHeld == true) {
                runCatching { wakeLock.release() }
            }
        }
    }

    suspend fun refreshToday(context: Context, courses: List<CourseEntity>, config: ScheduleConfigEntity, periods: List<PeriodEntity>) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val signature = scheduleSignature(courses, config, periods)
        val todayIsInTerm = scheduleWeekForDateOrNull(
            config,
            LocalDate.now()
        ) != null
        if (!todayIsInTerm || prefs.getString(KEY_SCHEDULE_SIGNATURE, null) != signature) {
            // Always clear stale alarms outside the term, even if this process has
            // already seen today's signature before the boundary check was fixed.
            scheduleToday(context, courses, config, periods)
            prefs.edit {putString(KEY_SCHEDULE_SIGNATURE, signature)}
        }
        // 清除旧版本实时活动功能可能遗留的通知与服务状态。
        NotificationManagerCompat.from(context).cancel(LEGACY_LIVE_UPDATE_ID)
        context.getSharedPreferences(LEGACY_LIVE_UPDATE_PREFS, Context.MODE_PRIVATE).edit { clear() }
    }

    suspend fun scheduleToday(context: Context, courses: List<CourseEntity>, config: ScheduleConfigEntity, periods: List<PeriodEntity>) {
        createChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelPreviouslyScheduled(context, alarmManager)
        if (!config.notificationsEnabled) {
            return
        }
        val today = todayCourses(AppState(courses = courses, config = config, periods = periods))
        val now = System.currentTimeMillis()
        val scheduleZone = ZoneId.systemDefault()
        val scheduleDate = LocalDate.now(scheduleZone)
        val scheduledCodes = mutableListOf<Int>()
        today.forEach { course ->
            val start = courseStartTime(course, periods) ?: return@forEach
            val trigger = notificationTriggerEpochMillis(
                date = scheduleDate,
                time = start,
                leadMinutes = config.notificationLeadMinutes,
                zone = scheduleZone
            )
            if (trigger > now) {
                val courseEnd = courseEndTime(course, periods) ?: start
                val reminderEnd = scheduleDate.atTime(courseEnd).atZone(scheduleZone).toInstant().toEpochMilli()
                if (trigger < reminderEnd) {
                    val requestCode = course.requestCode()
                    scheduleAlarm(alarmManager, trigger, pendingIntent(context, course, periods, requestCode))
                    scheduledCodes += requestCode
                }
            }
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {putString(KEY_REQUEST_CODES, scheduledCodes.joinToString(","))}
    }

    internal fun scheduleSignature(
        courses: List<CourseEntity>,
        config: ScheduleConfigEntity,
        periods: List<PeriodEntity>,
        today: LocalDate = LocalDate.now()
    ): String {
        val coursePart = courses
            .sortedBy { it.id }
            .joinToString(";") {
                listOf(
                    it.id,
                    it.name,
                    it.location.orEmpty(),
                    it.weekday,
                    it.periods.joinToString(","),
                    it.weeks.joinToString(","),
                    it.weekParity.name
                ).joinToString(":")
            }
        val periodPart = periods.joinToString(";") { "${it.periodIndex},${it.startTime},${it.endTime}" }
        return listOf(
            today.toString(),
            config.totalWeeks,
            config.currentWeek,
            config.termStartDate.orEmpty(),
            config.autoCurrentWeek,
            config.notificationsEnabled,
            config.notificationLeadMinutes,
            config.notificationMode.name,
            config.liveUpdateActionsEnabled,
            config.liveUpdateChipTextMode.name,
            coursePart,
            periodPart
        ).joinToString("|")
    }

    internal fun notificationTriggerEpochMillis(
        date: LocalDate,
        time: LocalTime,
        leadMinutes: Int,
        zone: ZoneId = ZoneId.systemDefault()
    ): Long = date
        .atTime(time)
        .atZone(zone)
        .toInstant()
        .toEpochMilli() - leadMinutes.coerceAtLeast(0) * 60_000L

    private fun scheduleAlarm(alarmManager: AlarmManager, trigger: Long, pending: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, pending)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, pending)
        }
    }

    private fun cancelPreviouslyScheduled(context: Context, alarmManager: AlarmManager) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val codes = prefs.getString(KEY_REQUEST_CODES, "").orEmpty().split(",").mapNotNull { it.toIntOrNull() }
        codes.forEach { alarmManager.cancel(emptyPendingIntent(context, it)) }
        prefs.edit {remove(KEY_REQUEST_CODES)}
    }

    private fun pendingIntent(context: Context, course: CourseEntity, periods: List<PeriodEntity>, requestCode: Int = course.requestCode()): PendingIntent {
        val intent = Intent(context, CourseAlarmReceiver::class.java)
            .putExtra("courseName", course.name)
            .putExtra("location", course.location ?: "")
            .putExtra("timeText", courseTimeLabel(course, periods))
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun emptyPendingIntent(context: Context, requestCode: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, requestCode, Intent(context, CourseAlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun CourseEntity.requestCode(): Int = (id * 10 % Int.MAX_VALUE).toInt()

    fun createChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, "课程提醒", NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun channelId(): String = CHANNEL_ID

    fun showTestNotification(context: Context, leadMinutes: Int): Boolean {
        createChannel(context)
        if (!canPostNotifications(context)) return false
        // 模拟一节「提前提醒分钟」后开始的课：标题沿用真实提醒格式，头部显示系统原生倒计时。
        val countdownEnd = System.currentTimeMillis() + leadMinutes.coerceIn(1, 180) * 60_000L
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("快上课了：英语")
            .setContentText("地点：公教楼110")
            .setShowWhen(true)
            .setUsesChronometer(true)
            .setWhen(countdownEnd)
            .setChronometerCountDown(true)
            .setAutoCancel(true)
            .build()
        return try {
            NotificationManagerCompat.from(context).notify(TEST_NOTIFICATION_ID, notification)
            true
        } catch (securityException: SecurityException) {
            false
        }
    }

    internal fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
}
