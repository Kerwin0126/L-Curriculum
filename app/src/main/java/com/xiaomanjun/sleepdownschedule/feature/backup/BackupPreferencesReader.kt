package com.xiaomanjun.sleepdownschedule.feature.backup

import com.xiaomanjun.sleepdownschedule.*

import com.xiaomanjun.sleepdownschedule.feature.agent.*

import com.xiaomanjun.sleepdownschedule.core.identity.AppIconManager

import android.content.Context

data class BackupPreferencesExport(
    val preferences: BackupPreferences,
    val additionalAssets: List<BackupAsset> = emptyList()
)

/**
 * Read-only adapter for non-Room user preferences. It exports only protocol-safe fields;
 * credential material is never loaded into the result.
 */
object BackupPreferencesReader {
    fun read(
        context: Context,
        scheduleStableIdsByRoomId: Map<Int, String>
    ): BackupPreferencesExport {
        return BackupPreferencesExport(
            preferences = BackupPreferences(
                preferencesVersion = BackupFormatV1.PREFERENCES_VERSION,
                appIcon = AppIconManager.backupPreferences(context),
                dayAgent = DayAgentPreferences.backupPreferences(context, scheduleStableIdsByRoomId)
            )
        )
    }
}
