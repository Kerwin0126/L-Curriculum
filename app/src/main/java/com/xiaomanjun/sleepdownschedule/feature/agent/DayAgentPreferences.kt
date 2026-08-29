package com.xiaomanjun.sleepdownschedule.feature.agent

import com.xiaomanjun.sleepdownschedule.feature.backup.BackupDayAgentPreferences
import com.xiaomanjun.sleepdownschedule.feature.backup.BackupStableId

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Process-independent Agent preferences.
 *
 * The preference file and every key intentionally remain unchanged so existing installations keep
 * their consent and options after this refactor. Memory keys are no longer read or written at
 * runtime, but backup round-trips still carry them so old backups keep restoring cleanly.
 */
object DayAgentPreferences {
    private const val Prefs = "day_agent_preferences"
    private const val MemoryMaxLength = 1200
    private val mutableChanges = MutableStateFlow(0L)
    val changes: Flow<Long> = mutableChanges

    fun hasDecision(context: Context): Boolean = prefs(context).getBoolean("has_decision", false)
    // 首次启用弹窗已移除：未做过启用决定（全新安装或旧版本未弹窗的用户）默认视为启用，
    // 明确关闭过的用户保持关闭。
    fun isEnabled(context: Context): Boolean {
        val storage = prefs(context)
        return if (!storage.getBoolean("has_decision", false)) true
        else storage.getBoolean("enabled", false)
    }
    fun isDailyAiEnabled(context: Context): Boolean = prefs(context).getBoolean("daily_ai_enabled", true)
    fun isWeatherEnabled(context: Context): Boolean = prefs(context).getBoolean("weather_enabled", true)

    fun backupPreferences(
        context: Context,
        scheduleStableIdsByRoomId: Map<Int, String>
    ): BackupDayAgentPreferences {
        val storage = prefs(context)
        val appliedActions = linkedMapOf<String, List<String>>()
        scheduleStableIdsByRoomId.forEach { (roomId, stableId) ->
            val actions = storage.getStringSet("applied_actions_$roomId", emptySet()).orEmpty().toList().sorted()
            if (actions.isNotEmpty()) appliedActions[stableId] = actions
        }
        return BackupDayAgentPreferences(
            hasDecision = storage.getBoolean("has_decision", false),
            enabled = storage.getBoolean("enabled", false),
            dailyAiEnabled = storage.getBoolean("daily_ai_enabled", true),
            weatherEnabled = storage.getBoolean("weather_enabled", true),
            memoryEnabled = storage.getBoolean("memory_enabled", false),
            memory = storage.getString("memory", null).orEmpty(),
            memoryTurnDay = storage.getString("memory_turn_day", null),
            memoryTurnCount = storage.getInt("memory_turn_count", 0),
            memoryLastAgentUpdateDay = storage.getString("memory_last_agent_update_day", null),
            appliedActionsBySchedule = appliedActions
        )
    }

    fun applyBackupPreferences(
        context: Context,
        backup: BackupDayAgentPreferences,
        scheduleRoomIdsByStableId: Map<String, Int>
    ) {
        backup.appliedActionsBySchedule.keys.forEach { stableId ->
            BackupStableId.requireValid(stableId, BackupStableId.SCHEDULE_PREFIX)
            require(stableId in scheduleRoomIdsByStableId) {
                "Day Agent applied action 引用了不存在的 schedule: $stableId"
            }
        }
        val storage = prefs(context)
        val editor = storage.edit()
        storage.all.keys
            .filter { it.startsWith("applied_actions_") }
            .forEach(editor::remove)
        editor
            .putBoolean("has_decision", backup.hasDecision)
            .putBoolean("enabled", backup.enabled)
            .putBoolean("daily_ai_enabled", backup.dailyAiEnabled)
            .putBoolean("weather_enabled", backup.weatherEnabled)
            .putBoolean("memory_enabled", backup.memoryEnabled)
            .putString("memory", backup.memory.take(MemoryMaxLength))
            .putInt("memory_turn_count", backup.memoryTurnCount.coerceAtLeast(0))
        if (backup.memoryTurnDay == null) editor.remove("memory_turn_day")
        else editor.putString("memory_turn_day", backup.memoryTurnDay)
        if (backup.memoryLastAgentUpdateDay == null) editor.remove("memory_last_agent_update_day")
        else editor.putString("memory_last_agent_update_day", backup.memoryLastAgentUpdateDay)
        backup.appliedActionsBySchedule.forEach { (stableId, actions) ->
            val targetRoomId = scheduleRoomIdsByStableId.getValue(stableId)
            editor.putStringSet("applied_actions_$targetRoomId", actions.toSet())
        }
        check(editor.commit()) { "无法提交 Day Agent preferences" }
        mutableChanges.value += 1
    }

    fun setEnabled(context: Context, enabled: Boolean, markDecided: Boolean = true) {
        prefs(context).edit {
                putBoolean("enabled", enabled)
                .putBoolean("has_decision", markDecided)
            }
        mutableChanges.value += 1
    }

    fun saveOptions(context: Context, dailyAiEnabled: Boolean, weatherEnabled: Boolean) {
        prefs(context).edit {
                putBoolean("daily_ai_enabled", dailyAiEnabled)
                .putBoolean("weather_enabled", weatherEnabled)
            }
        mutableChanges.value += 1
    }

    private fun prefs(context: Context) = context.getSharedPreferences(Prefs, Context.MODE_PRIVATE)
}
