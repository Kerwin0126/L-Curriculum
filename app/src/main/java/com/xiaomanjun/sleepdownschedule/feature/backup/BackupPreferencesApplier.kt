package com.xiaomanjun.sleepdownschedule.feature.backup

import com.xiaomanjun.sleepdownschedule.*

import com.xiaomanjun.sleepdownschedule.feature.agent.*

import com.xiaomanjun.sleepdownschedule.core.identity.AppIconManager

import android.content.Context
import java.io.File

/** Applies only the protocol's non-secret preference whitelist after the Room commit. */
object BackupPreferencesApplier {
    fun apply(
        context: Context,
        preferences: BackupPreferences,
        scheduleRoomIdsByStableId: Map<String, Int>,
        finalAssetFilesById: Map<String, File>
    ) {
        preferences.appIcon?.let { AppIconManager.applyBackupPreferences(context, it) }
        preferences.dayAgent?.let {
            DayAgentPreferences.applyBackupPreferences(context, it, scheduleRoomIdsByStableId)
        }
    }
}
