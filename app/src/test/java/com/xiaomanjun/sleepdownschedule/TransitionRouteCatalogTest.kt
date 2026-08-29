package com.xiaomanjun.sleepdownschedule

import com.xiaomanjun.sleepdownschedule.transition.AnchoredLegacyProfileId
import com.xiaomanjun.sleepdownschedule.transition.LegacyTransitionProfile
import com.xiaomanjun.sleepdownschedule.transition.TransitionNativeClosePolicy
import com.xiaomanjun.sleepdownschedule.transition.TransitionNativePolicy
import com.xiaomanjun.sleepdownschedule.transition.TransitionRouteCatalog
import com.xiaomanjun.sleepdownschedule.transition.TransitionRouteId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransitionRouteCatalogTest {
    @Test
    fun everyRouteIdHasExactlyOneCatalogEntryAndWireName() {
        val routes = TransitionRouteCatalog.all()
        assertEquals(TransitionRouteId.entries.size, routes.size)
        assertEquals(routes.size, routes.map { it.id }.distinct().size)
        assertEquals(routes.size, routes.map { it.id.wireName }.distinct().size)
        assertTrue(routes.all { it.destinationClassName.startsWith("com.xiaomanjun.sleepdownschedule.") })
    }

    @Test
    fun onlyAnchoredRoutesCanEnterTheOplusAllowlist() {
        val nativeCandidates = TransitionRouteCatalog.all().filter {
            it.nativePolicy == TransitionNativePolicy.OplusAllowlisted
        }
        assertEquals(
            setOf(
                TransitionRouteId.HomeToCourseManagement,
                TransitionRouteId.CourseManagementToDetail
            ),
            nativeCandidates.map { it.id }.toSet()
        )
        assertTrue(nativeCandidates.all { it.legacyProfile is LegacyTransitionProfile.Anchored })
        assertTrue(nativeCandidates.all { it.requiresOpeningAnchor && it.requiresReturnAnchor })
        assertTrue(
            TransitionRouteCatalog.all()
                .filter { it.nativePolicy == TransitionNativePolicy.Never }
                .all { it.nativeDestinationClassName == null }
        )
    }

    @Test
    fun menuOriginRoutesUseNativeOpenButLegacyClose() {
        assertEquals(
            TransitionNativeClosePolicy.LegacyOnly,
            TransitionRouteCatalog.get(TransitionRouteId.HomeToCourseManagement).nativeClosePolicy
        )
        assertEquals(
            TransitionNativeClosePolicy.MatchOpen,
            TransitionRouteCatalog.get(TransitionRouteId.CourseManagementToDetail).nativeClosePolicy
        )
    }

    @Test
    fun validatedNativeRoutesUseConcreteOpaqueActivities() {
        val detail = TransitionRouteCatalog.get(TransitionRouteId.CourseManagementToDetail)
        val courseManagement = TransitionRouteCatalog.get(TransitionRouteId.HomeToCourseManagement)
        assertEquals(
            "com.xiaomanjun.sleepdownschedule.OplusCourseManagementActivity",
            courseManagement.nativeDestinationClassName
        )
        assertEquals(
            "com.xiaomanjun.sleepdownschedule.OplusCourseManagementDetailActivity",
            detail.nativeDestinationClassName
        )
        assertTrue(
            TransitionRouteCatalog.all()
                .filter {
                    it.id !in setOf(
                        TransitionRouteId.CourseManagementToDetail,
                        TransitionRouteId.HomeToCourseManagement
                    )
                }
                .all { it.nativeDestinationClassName == null }
        )
    }

    @Test
    fun validatedLegacyProfilesStayBoundToTheirOriginalRoutes() {
        fun profile(route: TransitionRouteId) =
            (TransitionRouteCatalog.get(route).legacyProfile as LegacyTransitionProfile.Anchored)
                .profileId

        assertEquals(
            AnchoredLegacyProfileId.HomeMenuDestination,
            profile(TransitionRouteId.HomeToCourseManagement)
        )
        assertEquals(
            AnchoredLegacyProfileId.CourseManagementDetail,
            profile(TransitionRouteId.CourseManagementToDetail)
        )
    }
}
