package com.xiaomanjun.sleepdownschedule.core.remoteconfig

import com.xiaomanjun.sleepdownschedule.*

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class RemoteConfigStore(context: Context) {
    private val prefs = context.getSharedPreferences(PrefName, Context.MODE_PRIVATE)

    data class Cached(val bootstrap: RemoteBootstrap, val etag: String?, val lastFetchTimeMillis: Long)

    fun load(): Cached? {
        val encoded = prefs.getString(KeyBootstrap, null) ?: return null
        val bootstrap = RemoteConfigCacheCodec.decode(encoded) ?: return null
        return Cached(bootstrap, prefs.getString(KeyEtag, null), prefs.getLong(KeyLastFetch, 0L))
    }

    fun save(bootstrap: RemoteBootstrap, etag: String?, fetchedAtMillis: Long = System.currentTimeMillis()) {
        prefs.edit(commit = true) {
            putString(KeyBootstrap, RemoteConfigCacheCodec.encode(bootstrap))
            if (etag == null) remove(KeyEtag) else putString(KeyEtag, etag)
            putLong(KeyLastFetch, fetchedAtMillis)
            putLong(KeyReceivedWall, fetchedAtMillis)
        }
    }

    fun markNotModified(fetchedAtMillis: Long = System.currentTimeMillis()) {
        prefs.edit { putLong(KeyLastFetch, fetchedAtMillis) }
    }

    companion object {
        private const val PrefName = "sleepdown_remote_config"
        private const val KeyBootstrap = "bootstrap_json_v1"
        private const val KeyEtag = "bootstrap_etag_v1"
        private const val KeyLastFetch = "last_fetch_time_v1"
        private const val KeyReceivedWall = "received_wall_time_v1"
    }
}

internal object RemoteConfigCacheCodec {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun encode(bootstrap: RemoteBootstrap): String = json.encodeToString(bootstrap)

    fun decode(encoded: String): RemoteBootstrap? = runCatching {
        json.decodeFromString<RemoteBootstrap>(encoded)
    }.getOrNull()
}
