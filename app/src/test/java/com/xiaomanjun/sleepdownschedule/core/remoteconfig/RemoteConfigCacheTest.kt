package com.xiaomanjun.sleepdownschedule.core.remoteconfig

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteConfigCacheTest {
    @Test
    fun bootstrapCacheRoundTripsAndRejectsCorruption() {
        val original = RemoteBootstrap(
            schemaVersion = 1,
            serverTime = 1_787_000_000,
            agreements = RemoteAgreementSet(
                privacy = RemoteAgreementSummary(4, "隐私政策", "https://api.sleepdownschedule.cn/public/agreements/privacy/4", true, true)
            ),
            donations = RemoteDonationSection(
                published = true,
                title = "捐赠致谢",
                entries = listOf(RemoteDonationEntry(1, "sleep-friend", 1234, "CNY"))
            )
        )

        val encoded = RemoteConfigCacheCodec.encode(original)
        assertEquals(original, RemoteConfigCacheCodec.decode(encoded))
        assertNull(RemoteConfigCacheCodec.decode("{not-json"))
    }
}
