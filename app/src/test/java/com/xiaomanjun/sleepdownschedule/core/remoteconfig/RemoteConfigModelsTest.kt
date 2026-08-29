package com.xiaomanjun.sleepdownschedule.core.remoteconfig

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RemoteConfigModelsTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun bootstrapParsesUnknownFieldsAndOptionalSections() {
        val bootstrap = json.decodeFromString<RemoteBootstrap>(
            """{"schemaVersion":1,"serverTime":1787000000,"agreements":{"privacy":null,"terms":null},"future":true}"""
        )
        assertEquals(1, bootstrap.schemaVersion)
        assertEquals(1787000000L, bootstrap.serverTime)
        assertFalse(bootstrap.donations.published)
        assertEquals(RemoteAgreementSet(), bootstrap.agreements)
    }
}
