package com.xiaomanjun.sleepdownschedule.core.remoteconfig

import kotlinx.serialization.Serializable

@Serializable
data class RemoteBootstrap(
    val schemaVersion: Int,
    val serverTime: Long,
    val agreements: RemoteAgreementSet = RemoteAgreementSet(),
    val transitions: RemoteTransitionConfig = RemoteTransitionConfig()
)

/** Server-side kill switches. Missing or stale configuration always keeps native transitions off. */
@Serializable
data class RemoteTransitionConfig(
    val oplusViewSeamlessEnabled: Boolean = false,
    val oplusRouteAllowlist: Set<String> = emptySet()
)

@Serializable
data class RemoteAgreementSet(
    val privacy: RemoteAgreementSummary? = null,
    val terms: RemoteAgreementSummary? = null
)

@Serializable
data class RemoteAgreementSummary(
    val version: Long,
    val title: String,
    val url: String,
    val required: Boolean,
    val forceReaccept: Boolean
)

@Serializable
data class InstallationPayload(
    val installationId: String,
    val versionCode: Long,
    val versionName: String,
    val androidApi: Int,
    val deviceModel: String
)

data class RemoteExperienceState(
    val agreement: RemoteAgreementSummary? = null
)

data class RemoteConfigState(
    val bootstrap: RemoteBootstrap? = null,
    val lastFetchTimeMillis: Long = 0L,
    val lastError: String? = null,
    val isRefreshing: Boolean = false
)
