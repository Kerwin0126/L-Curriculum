package com.xiaomanjun.sleepdownschedule.app.ui

import com.xiaomanjun.sleepdownschedule.*
import com.xiaomanjun.sleepdownschedule.app.startup.*
import com.xiaomanjun.sleepdownschedule.app.state.*
import com.xiaomanjun.sleepdownschedule.core.ui.designsystem.*
import com.xiaomanjun.sleepdownschedule.glass.ui.*
import com.xiaomanjun.sleepdownschedule.transition.legacy.*
import com.xiaomanjun.sleepdownschedule.core.ui.settings.*
import com.xiaomanjun.sleepdownschedule.feature.settings.*
import com.xiaomanjun.sleepdownschedule.feature.course.management.HomeMenuActivitySourceFallback
import com.xiaomanjun.sleepdownschedule.feature.course.management.putCourseManagementInitialState
import com.xiaomanjun.sleepdownschedule.feature.schedule.*
import com.xiaomanjun.sleepdownschedule.feature.schedule.manager.*
import com.xiaomanjun.sleepdownschedule.feature.schedule.picker.*
import com.xiaomanjun.sleepdownschedule.feature.home.*
import com.xiaomanjun.sleepdownschedule.feature.home.day.*
import com.xiaomanjun.sleepdownschedule.feature.home.week.*
import com.xiaomanjun.sleepdownschedule.feature.home.overlay.*

import com.xiaomanjun.sleepdownschedule.app.config.SleepDownRemoteConfig
import com.xiaomanjun.sleepdownschedule.core.performance.*
import com.xiaomanjun.sleepdownschedule.core.remoteconfig.*
import com.xiaomanjun.sleepdownschedule.core.wallpaper.*
import com.xiaomanjun.sleepdownschedule.domain.course.*
import com.xiaomanjun.sleepdownschedule.feature.course.editor.*
import com.xiaomanjun.sleepdownschedule.feature.importing.*

import com.xiaomanjun.sleepdownschedule.feature.backup.*
import com.xiaomanjun.sleepdownschedule.feature.widget.*

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.Application
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.net.toUri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.os.Build
import android.view.WindowManager
import android.view.PixelCopy
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android.view.HapticFeedbackConstants
import android.view.WindowInsetsController
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.content.FileProvider
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField as MaterialOutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.MiuixPopupHost
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.viewinterop.AndroidView
import androidx.palette.graphics.Palette
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs
import com.kyant.backdrop.catalog.components.LiquidButton
import com.kyant.backdrop.catalog.components.LiquidButtonPressSnapshot
import com.kyant.backdrop.catalog.components.LiquidPanel
import com.kyant.backdrop.catalog.components.LiquidSlider
import com.kyant.backdrop.catalog.components.LiquidToggle
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.shapes.RoundedRectangle
import com.xiaomanjun.sleepdownschedule.glass.GlassBackdropDomain
import com.xiaomanjun.sleepdownschedule.glass.CourseGlassOcclusionPhase
import com.xiaomanjun.sleepdownschedule.glass.GlassRenderPhase
import com.xiaomanjun.sleepdownschedule.glass.GlassSamplingLink
import com.xiaomanjun.sleepdownschedule.glass.GlassTopologyNode
import com.xiaomanjun.sleepdownschedule.glass.GlassTopologyNodeRole
import com.xiaomanjun.sleepdownschedule.glass.LocalGlassSceneState
import com.xiaomanjun.sleepdownschedule.glass.glassBackdropProducer
import com.xiaomanjun.sleepdownschedule.glass.rememberGlassCombinedBackdrop
import com.xiaomanjun.sleepdownschedule.glass.rememberGlassLayerBackdrop
import com.xiaomanjun.sleepdownschedule.glass.rememberGlassSceneState
import com.xiaomanjun.sleepdownschedule.glass.GlassBackendPolicy
import com.xiaomanjun.sleepdownschedule.glass.CourseGlassMaterialRevealDurationMillis
import com.xiaomanjun.sleepdownschedule.glass.CourseGlassOcclusionTrace
import com.xiaomanjun.sleepdownschedule.glass.CourseGlassRestoreCadenceNanos
import com.xiaomanjun.sleepdownschedule.glass.CourseGlassRestoreRegistry
import com.xiaomanjun.sleepdownschedule.glass.LocalCourseGlassMaterialRevealProgress
import com.xiaomanjun.sleepdownschedule.glass.LocalCourseGlassRestoreRegistry
import com.xiaomanjun.sleepdownschedule.glass.shouldSuspendCourseGlassMaterials
import com.xiaomanjun.sleepdownschedule.transition.ActivityTransitionCoordinator
import com.xiaomanjun.sleepdownschedule.transition.CrossActivityTransitionHost
import com.xiaomanjun.sleepdownschedule.transition.LegacyTransitionProfile
import com.xiaomanjun.sleepdownschedule.transition.StaticTransitionAnchorProvider
import com.xiaomanjun.sleepdownschedule.transition.TransitionAnchorFrame
import com.xiaomanjun.sleepdownschedule.transition.TransitionLaunchResult
import com.xiaomanjun.sleepdownschedule.transition.TransitionPayload
import com.xiaomanjun.sleepdownschedule.transition.TransitionRouteCatalog
import com.xiaomanjun.sleepdownschedule.transition.TransitionRouteId
import com.xiaomanjun.sleepdownschedule.transition.openRegisteredActivity
import com.xiaomanjun.sleepdownschedule.transition.transitionRouteIdOrNull
import top.yukonga.miuix.kmp.squircle.squircleClip
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.DisposableEffect
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicBoolean
import java.io.File
import java.net.HttpURLConnection
import java.net.URLDecoder
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.ceil
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private fun performButtonHaptic(view: android.view.View) {
    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
}

private fun android.view.Window.setStatusBarDarkIcons(darkIcons: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            if (darkIcons) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = if (darkIcons) {
            decorView.systemUiVisibility or android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decorView.systemUiVisibility and android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

sealed interface Screen {
    data object Home : Screen
    data object Config : Screen
}

enum class HomeMode { Day, Week }
enum class SettingsSection { Schedule, Notifications }
enum class SettingsPage { Root, General, LiquidGlass, Widgets, DayAgent, Schedule, Notifications, ScheduleManager, BackupRestore, BackupPreview, About, Changelog, PrivacyPolicy }

/** Matches the navigation motion used by the bundled Miuix system-style navigator. */
private class MiuixSettingsNavigationEasing(
    response: Float = 0.8f,
    damping: Float = 0.95f
) : Easing {
    private val decay: Float
    private val frequency: Float
    private val phase: Float

    init {
        val omega = 2.0 * PI / response
        val stiffness = omega * omega
        val friction = damping * 4.0 * PI / response
        frequency = (sqrt(4.0 * stiffness - friction * friction) / 2.0).toFloat()
        decay = (-friction / 2.0).toFloat()
        phase = decay / frequency
    }

    override fun transform(fraction: Float): Float {
        val time = fraction.toDouble()
        val attenuation = exp(decay * time)
        return (attenuation * (-cos(frequency * time) + phase * sin(frequency * time)) + 1.0).toFloat()
    }
}

private val MiuixSettingsNavigationMotion = MiuixSettingsNavigationEasing()

/** Follow-through state for the three-dot button after a morph surface hands off on close. */
private data class SourceButtonFollowThrough(
    val startCenter: Offset,
    val startScale: Float
)

private const val SettingsDetailPageExtra = "settings_page"
private const val BackupPreviewUriExtra = "backup_preview_uri"

private fun SettingsPage.title(): String = when (this) {
    SettingsPage.Root -> "设置"
    SettingsPage.General -> "通用设置"
    SettingsPage.LiquidGlass -> "液态玻璃"
    SettingsPage.Widgets -> "小组件设置"
    SettingsPage.DayAgent -> "今日助手"
    SettingsPage.Schedule -> "课表详细设置"
    SettingsPage.Notifications -> "通知设置"
    SettingsPage.ScheduleManager -> "课表设置"
    SettingsPage.BackupRestore -> "备份与恢复"
    SettingsPage.BackupPreview -> "恢复预览"
    SettingsPage.About -> "关于应用"
    SettingsPage.Changelog -> "关于应用"
	SettingsPage.PrivacyPolicy -> "隐私政策"
}

internal fun SettingsPage.usesPersistentCenteredSettingsTitle(): Boolean = when (this) {
    SettingsPage.LiquidGlass,
    SettingsPage.Widgets,
    SettingsPage.About,
	SettingsPage.Changelog,
	SettingsPage.PrivacyPolicy -> true
    else -> false
}

internal data class TabletSettingsNavigationState(
    val rootPage: SettingsPage = SettingsPage.General,
    val detailPages: List<SettingsPage> = emptyList()
) {
    val displayedPage: SettingsPage
        get() = detailPages.lastOrNull() ?: rootPage

    fun selectRoot(page: SettingsPage): TabletSettingsNavigationState = copy(
        rootPage = page.takeUnless { it == SettingsPage.Root } ?: SettingsPage.General,
        detailPages = emptyList()
    )

    fun pushDetail(page: SettingsPage): TabletSettingsNavigationState =
        if (page == displayedPage) this else copy(detailPages = detailPages + page)

    fun popDetail(): TabletSettingsNavigationState =
        if (detailPages.isEmpty()) this else copy(detailPages = detailPages.dropLast(1))
}

private val TabletSettingsNavigationStateSaver = listSaver<TabletSettingsNavigationState, String>(
    save = { state -> listOf(state.rootPage.name) + state.detailPages.map(SettingsPage::name) },
    restore = { names ->
        val root = names.firstOrNull()
            ?.let { runCatching { SettingsPage.valueOf(it) }.getOrNull() }
            ?.takeUnless { it == SettingsPage.Root }
            ?: SettingsPage.General
        val details = names.drop(1).mapNotNull { name ->
            runCatching { SettingsPage.valueOf(name) }.getOrNull()
        }
        TabletSettingsNavigationState(rootPage = root, detailPages = details)
    }
)

internal val DayDockScrollPadding = 104.dp
internal val WeekDockScrollPadding = 132.dp
internal val DockScrollPadding = 132.dp
internal val HomeHeaderGlassBlur = 2.dp
internal val HomeHeaderGlassLensHeight = 12.dp
internal val HomeHeaderGlassLensAmount = 24.dp
internal val HomeLightGlassSurfaceColor = ComposeColor(0xFFF2F4F8)
internal val HomeLightGlassGradientColor = ComposeColor(0xFFF7F8FB)
internal val HomeLightGlassAccentColor = ComposeColor(0xFF0A84FF)
internal val HomeLightGlassSelectedAccentColor = ComposeColor(0xFF006FD6)
internal const val HomeLightGlassChromeTintAlpha = 0.54f
internal const val HomeLightGlassAccentTintAlpha = 0.24f
internal const val HomeLightGlassPanelTintAlpha = 0.15f
internal const val HomeLightGlassMenuTintAlpha = 0.14f
internal fun homeChromeGlassSurfaceAlpha(lightGlass: Boolean): Float =
    if (lightGlass) HomeLightGlassChromeTintAlpha else 0.45f
internal fun dockImeCompensationPx(imeBottomPx: Int, systemBottomPx: Int): Int =
    (imeBottomPx - systemBottomPx).coerceAtLeast(0)
internal const val HomeHeaderGlassHighlightAlpha = 0.09f
internal const val HomeHeaderGlassShadowAlpha = 0.05f
internal const val HomeHeaderGlassOuterShadowAlpha = 0.018f
internal const val HomeHeaderGlassInnerShadowAlpha = 0.08f
internal fun homeChromeBlur(base: Dp, config: ScheduleConfigEntity): Dp =
    base * normalizedHomeChromeBlurScale(config.homeChromeBlurScale)
internal fun homeHeaderGlassTokens(lightGlass: Boolean, blurScale: Float = DefaultHomeChromeBlurScale): GlassTokens {
    val base = GlassTokens.pill(intensity = 0.95f).copy(
        surfaceAlpha = homeChromeGlassSurfaceAlpha(lightGlass)
    )
    return base.copy(blur = base.blur * normalizedHomeChromeBlurScale(blurScale))
}

sealed interface HomeDialog {
    data object ImportSchedule : HomeDialog
    data class ConfirmImport(val draft: ImportDraft, val returnDialog: HomeDialog? = ImportSchedule) : HomeDialog
    data class EditWallpaper(val uri: Uri, val entrySnapshot: Bitmap?) : HomeDialog
    data class EditCourse(val course: CourseEntity?, val targetWeek: Int? = null) : HomeDialog
    data class ApplyCourseEdit(val original: CourseEntity, val edited: CourseEntity, val targetWeek: Int) : HomeDialog
    data class ConfirmCourseConflicts(
        val original: CourseEntity,
        val edited: CourseEntity,
        val targetWeek: Int,
        val conflictWeeks: List<Int>,
        val singleWeekOnly: Boolean = false
    ) : HomeDialog
    data class ApplyCourseDelete(val course: CourseEntity, val targetWeek: Int) : HomeDialog
}

private data class PendingCourseGroupEdit(
    val originals: List<CourseEntity>,
    val edited: List<CourseEntity>
)

private fun composeDetailMorphSnapshot(underlay: Bitmap, popup: Bitmap): Bitmap? = runCatching {
    val width = underlay.width
    val height = underlay.height
    require(width > 0 && height > 0)
    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { result ->
        val target = android.graphics.Rect(0, 0, width, height)
        AndroidCanvas(result).apply {
            drawBitmap(underlay, null, target, null)
            drawBitmap(popup, null, target, null)
        }
    }
}.getOrNull()

private data class DetailMorphWindowSnapshot(
    val bitmap: Bitmap,
    val originInWindow: Offset
)

/**
 * Captures the final pixels of the special schedule-picker scene.
 *
 * That scene is assembled from a nested PickerScene producer, bitmap-backed schedule cards and a
 * root Miuix popup-host sibling. Recording an ancestor GraphicsLayer is still useful as a fallback,
 * but it is not guaranteed to flatten every nested RenderNode on ColorOS. PixelCopy reads the
 * already-composited source window once, before the destination Activity exists.
 */
private suspend fun captureDetailMorphWindowSnapshot(
    activity: Activity,
    rootPositionInWindow: Offset,
    rootSize: IntSize
): DetailMorphWindowSnapshot? {
    val decor = activity.window.decorView
    if (decor.width <= 0 || decor.height <= 0 || rootSize.width <= 0 || rootSize.height <= 0) {
        return null
    }
    val left = rootPositionInWindow.x.roundToInt().coerceIn(0, decor.width - 1)
    val top = rootPositionInWindow.y.roundToInt().coerceIn(0, decor.height - 1)
    val right = (rootPositionInWindow.x + rootSize.width)
        .roundToInt()
        .coerceIn(left + 1, decor.width)
    val bottom = (rootPositionInWindow.y + rootSize.height)
        .roundToInt()
        .coerceIn(top + 1, decor.height)
    val sourceRect = android.graphics.Rect(left, top, right, bottom)
    val bitmap = Bitmap.createBitmap(
        sourceRect.width(),
        sourceRect.height(),
        Bitmap.Config.ARGB_8888
    )
    return suspendCancellableCoroutine { continuation ->
        runCatching {
            PixelCopy.request(
                activity.window,
                sourceRect,
                bitmap,
                { result ->
                    if (!continuation.isActive) {
                        bitmap.recycle()
                    } else if (result == PixelCopy.SUCCESS) {
                        continuation.resume(
                            DetailMorphWindowSnapshot(
                                bitmap = bitmap,
                                originInWindow = Offset(left.toFloat(), top.toFloat())
                            )
                        )
                    } else {
                        bitmap.recycle()
                        continuation.resume(null)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }.onFailure {
            bitmap.recycle()
            if (continuation.isActive) continuation.resume(null)
        }
    }
}

private var splashEntranceDone = false
internal val LocalEditingCourseId = compositionLocalOf<Long?> { null }
internal val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
@Volatile
internal var hideFromRecentsEnabled = false

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CourseScheduleAppUi(
    viewModel: ScheduleViewModel,
    externalIcsUri: Uri? = null,
    onExternalIcsConsumed: (Uri) -> Unit = {},
    onStartupContentReady: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val allSchedulesState by viewModel.allSchedulesState.collectAsStateWithLifecycle()
    val message by viewModel.snackbar.collectAsStateWithLifecycle()
    val pickerState = rememberSchedulePickerState()
    var previewScheduleId by remember { mutableStateOf<Int?>(null) }
    var pendingPickerEditorScheduleId by remember { mutableStateOf<Int?>(null) }
    var quickScheduleDraft by remember { mutableStateOf<QuickScheduleDraft?>(null) }
    var detailMorphState by remember { mutableStateOf<DetailMorphState>(DetailMorphState.Idle) }
    var detailMorphRequest by remember { mutableStateOf<DetailMorphRequest?>(null) }
    var detailCaptureCoverBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var detailCaptureRecordCleanFrame by remember { mutableStateOf(false) }
    val detailCaptureMaskActive = remember { AtomicBoolean(false) }
    val baseVisualState = previewScheduleId?.let(allSchedulesState::forSchedule) ?: state
    var personalizationDraftConfig by remember { mutableStateOf<ScheduleConfigEntity?>(null) }
    var personalizationPendingCommitConfig by remember { mutableStateOf<ScheduleConfigEntity?>(null) }
    var personalizationSliderPreviewKey by remember { mutableStateOf<String?>(null) }
    val personalizationPreviewState = remember { PersonalizationPreviewState() }
    val personalizationPreviewProgress by animateFloatAsState(
        targetValue = if (personalizationSliderPreviewKey != null) 1f else 0f,
        animationSpec = tween(durationMillis = 180, easing = CubicBezierEasing(0.22f, 0f, 0.2f, 1f)),
        label = "personalization-preview-transition"
    )
    LaunchedEffect(baseVisualState.config, personalizationPendingCommitConfig) {
        val pending = personalizationPendingCommitConfig
        if (pending != null && baseVisualState.config == pending) {
            personalizationPendingCommitConfig = null
        }
    }
    LaunchedEffect(baseVisualState.config.id) {
        if (personalizationDraftConfig?.id != baseVisualState.config.id) {
            personalizationDraftConfig = null
        }
        if (personalizationPendingCommitConfig?.id != baseVisualState.config.id) {
            personalizationPendingCommitConfig = null
        }
    }
    val visualState = (personalizationDraftConfig ?: personalizationPendingCommitConfig)
        ?.takeIf { it.id == baseVisualState.config.id }
        ?.let { baseVisualState.copy(config = it) }
        ?: baseVisualState
    val glassBackendPolicy = remember { GlassBackendPolicy.LargeGlass }
    val glassSceneState = rememberGlassSceneState(
        sceneId = "home",
        backendPolicy = glassBackendPolicy
    )
    val screenGraphicsLayer = rememberGraphicsLayer()
    // The week grid is already recorded into this layer for Morph motion. Reuse that GPU layer
    // as the personalization backdrop producer so the glass does not traverse every live
    // week-card RenderNode again on each animation frame.
    val cachedWeekHomeBackdrop = rememberGlassLayerBackdrop(
        domain = GlassBackdropDomain.Content,
        providerId = "home-cached-week-scene",
        sceneState = glassSceneState,
        graphicsLayer = screenGraphicsLayer
    )
    val detailScreenGraphicsLayer = rememberGraphicsLayer()
    val detailPopupGraphicsLayer = rememberGraphicsLayer()
    var detailPopupCaptureActive by remember { mutableStateOf(false) }
    var detailPopupCaptureToken by remember { mutableIntStateOf(0) }
    val detailPopupCapturedToken = remember { AtomicInteger(-1) }
    val recordedScheduleId = remember { AtomicInteger(-1) }
    val recordedHomeGeneration = remember { AtomicLong(0L) }
    val lastRecordedHomeFrameKey = remember { AtomicReference<String?>(null) }
    val weekHomeSurfaceUsesOffscreenCache = remember { AtomicBoolean(false) }
    var captureRenderToken by remember { mutableIntStateOf(0) }
    var snapshotGeneration by remember { mutableIntStateOf(0) }
    var snapshotJob by remember { mutableStateOf<Job?>(null) }
    var cacheHydrationJob by remember { mutableStateOf<Job?>(null) }
    var entryPrewarmJob by remember { mutableStateOf<Job?>(null) }
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var settingsExitInterceptionRequired by remember { mutableStateOf(false) }
    var settingsExitRequest by remember { mutableIntStateOf(0) }
    var pendingSettingsExitAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var homeMode by remember { mutableStateOf(if (state.config.defaultHomeMode == HomeStartMode.DAY) HomeMode.Day else HomeMode.Week) }
    LaunchedEffect(state.config.defaultHomeMode) {
        homeMode = if (state.config.defaultHomeMode == HomeStartMode.DAY) HomeMode.Day else HomeMode.Week
    }
    var homeDialog by remember { mutableStateOf<HomeDialog?>(null) }
    var renderedHomeDialog by remember { mutableStateOf<HomeDialog?>(null) }
    var homeDialogVisible by remember { mutableStateOf(false) }
    val appScope = rememberCoroutineScope()
    var courseGlassOcclusionPhase by remember {
        mutableStateOf(CourseGlassOcclusionPhase.Live)
    }
    val courseGlassRestoreRegistry = remember { CourseGlassRestoreRegistry() }
    var courseGlassOcclusionGeneration by remember { mutableIntStateOf(0) }
    var courseGlassRestoredGroupKeys by remember { mutableStateOf<Set<String>>(emptySet()) }
    var courseGlassFrozenRestoreGroupKeys by remember { mutableStateOf<List<String>>(emptyList()) }
    var courseGlassForceCacheReplay by remember { mutableStateOf(false) }
    var courseGlassFlatCacheDrawRequest by remember { mutableIntStateOf(0) }
    val courseGlassFlatCacheRecordedRequest = remember { AtomicInteger(0) }
    val courseGlassMaterialRevealProgress = remember { Animatable(1f) }
    val courseGlassMaterialRevealProgressProvider: () -> Float = remember {
        { courseGlassMaterialRevealProgress.value }
    }
    var courseGlassSessionScheduleId by remember { mutableIntStateOf(-1) }
    var courseGlassSessionWeek by remember { mutableIntStateOf(-1) }
    var courseGlassSessionWidthDp by remember { mutableIntStateOf(-1) }
    var courseGlassSessionHeightDp by remember { mutableIntStateOf(-1) }
    var courseEditorRequest by remember { mutableStateOf<CourseEditorOverlayRequest?>(null) }
    var pendingCourseGroupEdit by remember { mutableStateOf<PendingCourseGroupEdit?>(null) }
    var pendingCourseGroupDelete by remember { mutableStateOf<List<CourseEntity>>(emptyList()) }
    var courseEditorRenderedCourseId by remember { mutableStateOf<Long?>(null) }
    val courseEditorMotionState = rememberCourseEditorMotionState()
    val courseEditorOverlayPhase = courseEditorMotionState.phase
    fun openCourseEditor(course: CourseEntity, targetWeek: Int?, sourceBounds: Rect?) {
        if (courseEditorRequest != null) return
        courseEditorRequest = CourseEditorOverlayRequest(
            course = course,
            targetWeek = targetWeek,
            sourceBoundsInRoot = sourceBounds,
            sourceIsDayCard = homeMode == HomeMode.Day && sourceBounds != null
        )
    }
    fun closeCourseEditor() {
        courseEditorRequest = null
    }
    fun dismissHomeDialog() {
        homeDialogVisible = false
        homeDialog = null
        pendingCourseGroupEdit = null
        pendingCourseGroupDelete = emptyList()
    }
    LaunchedEffect(homeDialog) {
        if (homeDialog != null) {
            renderedHomeDialog = homeDialog
            homeDialogVisible = true
        } else if (renderedHomeDialog != null) {
            homeDialogVisible = false
            delay(320)
            renderedHomeDialog = null
        }
    }
    val homeAnchoredMorphState = rememberHomeAnchoredMorphState()
    val homeMenuDestinationMotionState = rememberHomeMenuDestinationMotionState()
    var homeMenuDestinationRequest by remember { mutableStateOf<HomeMenuDestinationRequest?>(null) }
    var homeMenuSourceHidden by remember { mutableStateOf(false) }
    var homeAddMenuBoundsInRoot by remember { mutableStateOf<Rect?>(null) }
    var homeAnchoredOverlayRequest by remember { mutableStateOf<HomeAnchoredOverlayRequest?>(null) }
    var jumpWeekDialogMounted by remember { mutableStateOf(false) }
    var jumpWeekDialogVisible by remember { mutableStateOf(false) }
    var pendingJumpWeekDialog by remember { mutableStateOf(false) }
    var pendingOpenScheduleSettings by remember { mutableStateOf(false) }
    var homeMenuActivityLaunched by remember { mutableStateOf(false) }
    var destinationOwnsButtonReturn by remember { mutableStateOf(false) }
    var destinationCollapseHandedOff by remember { mutableStateOf(false) }
    var addButtonBounds by remember { mutableStateOf<Rect?>(null) }
    var personalizeButtonBounds by remember { mutableStateOf<Rect?>(null) }
    var sourceButtonFollowThrough by remember { mutableStateOf<SourceButtonFollowThrough?>(null) }
    val sourceButtonFollowProgress = remember { Animatable(0f) }
    val latestSourceFollowThrough = rememberUpdatedState<(Rect) -> Unit> { rect ->
        if (sourceButtonFollowThrough == null) {
            val rest = addButtonBounds ?: return@rememberUpdatedState
            sourceButtonFollowThrough = SourceButtonFollowThrough(
                startCenter = rect.center,
                startScale = (rect.width / rest.width.coerceAtLeast(1f)).coerceIn(0.30f, 1.05f)
            )
        }
    }
    var pendingHomeAnchoredOverlay by remember { mutableStateOf<HomeAnchoredOverlayKind?>(null) }
    var pendingHomeAnchoredSourceScale by remember { mutableFloatStateOf(1f) }
    var showScheduleEntryPill by remember { mutableStateOf(false) }
    val editingCourseId: Long? = courseEditorRequest?.course?.id ?: courseEditorRenderedCourseId
    val activeHomeAnchoredOverlay =
        homeAnchoredOverlayRequest?.kind ?: homeAnchoredMorphState.renderedKind
    val destinationTransitionActive =
        homeMenuDestinationRequest != null ||
            homeMenuDestinationMotionState.phase != HomeAnchoredOverlayPhase.Idle
    val addButtonHidden = activeHomeAnchoredOverlay == HomeAnchoredOverlayKind.Add ||
        sourceButtonFollowThrough != null ||
        homeMenuActivityLaunched ||
        (destinationTransitionActive && !destinationCollapseHandedOff)

    fun openHomeAnchoredOverlay(kind: HomeAnchoredOverlayKind, sourcePressedScale: Float = 1f) {
        if (homeAnchoredOverlayRequest != null ||
            homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle
        ) return
        val bounds = when (kind) {
            HomeAnchoredOverlayKind.Add -> addButtonBounds
            HomeAnchoredOverlayKind.Personalize -> personalizeButtonBounds
        }
        if (bounds == null || bounds.width <= 2f || bounds.height <= 2f) {
            pendingHomeAnchoredOverlay = kind
            pendingHomeAnchoredSourceScale = sourcePressedScale
            return
        }
        pendingHomeAnchoredOverlay = null
        homeAnchoredOverlayRequest = HomeAnchoredOverlayRequest(kind, bounds, sourcePressedScale)
    }

    fun toggleHomeAnchoredOverlay(kind: HomeAnchoredOverlayKind, sourcePressedScale: Float = 1f) {
        if (homeAnchoredOverlayRequest?.kind == kind) {
            homeAnchoredOverlayRequest = null
        } else {
            openHomeAnchoredOverlay(kind, sourcePressedScale)
        }
    }

    val windowContainerSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val homeAdaptiveMetrics = rememberHomeAdaptiveMetrics()

    LaunchedEffect(
        pendingHomeAnchoredOverlay,
        addButtonBounds,
        personalizeButtonBounds,
        homeAnchoredMorphState.phase,
        screen
    ) {
        val pending = pendingHomeAnchoredOverlay ?: return@LaunchedEffect
        if (screen !is Screen.Home || homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle) {
            return@LaunchedEffect
        }
        openHomeAnchoredOverlay(pending, pendingHomeAnchoredSourceScale)
    }
    LaunchedEffect(screen) {
        if (screen !is Screen.Home) {
            pendingHomeAnchoredOverlay = null
            homeAnchoredOverlayRequest = null
            personalizationSliderPreviewKey = null
            personalizationDraftConfig = null
            personalizationPreviewState.clearAll()
        }
        if (screen !is Screen.Config) {
            settingsExitInterceptionRequired = false
            pendingSettingsExitAction = null
        }
    }
    LaunchedEffect(homeAnchoredOverlayRequest) {
        if (homeAnchoredOverlayRequest?.kind != HomeAnchoredOverlayKind.Personalize) {
            personalizationSliderPreviewKey = null
            personalizationDraftConfig = null
            personalizationPreviewState.clearAll()
        }
    }
    var homeContentUnderTopBar by remember { mutableStateOf(false) }
    val adaptiveWeekCardHeight = remember(
        windowContainerSize.height,
        density.density,
        density.fontScale,
        homeAdaptiveMetrics.weekTopSpacerHeight,
        homeAdaptiveMetrics.safeBottom,
        visualState.periods.size
    ) {
        adaptiveWeekRowHeightDp(
            viewportHeightDp = windowContainerSize.height / density.density.coerceAtLeast(0.001f),
            topSpacerDp = homeAdaptiveMetrics.weekTopSpacerHeight.value,
            periodCount = visualState.periods.size,
            bottomInsetDp = homeAdaptiveMetrics.safeBottom.value + 8f,
            fontScale = density.fontScale
        )
    }
    val weekCardHeightScaleFloor = remember(adaptiveWeekCardHeight, density.fontScale) {
        minimumWeekCardHeightScale(
            adaptiveRowHeightDp = adaptiveWeekCardHeight,
            fontScale = density.fontScale
        )
    }
    var weekCardHeightScale by remember(
        visualState.config.id,
        visualState.config.weekCardHeightScale,
        weekCardHeightScaleFloor
    ) {
        mutableFloatStateOf(
            normalizedWeekCardHeightScale(
                visualState.config.weekCardHeightScale,
                weekCardHeightScaleFloor
            )
        )
    }
    val weekCardHeight = adaptiveWeekCardHeight * weekCardHeightScale
    val context = LocalContext.current
    val remoteExperience by SleepDownRemoteConfig.experience.collectAsStateWithLifecycle()
    val remoteConfigState by SleepDownRemoteConfig.state.collectAsStateWithLifecycle()
    fun requestSettingsExit(action: () -> Unit) {
        if (screen is Screen.Config && settingsExitInterceptionRequired) {
            pendingSettingsExitAction = action
            settingsExitRequest++
        } else {
            action()
        }
    }
    BackHandler(enabled = screen is Screen.Config && settingsExitInterceptionRequired) {
        requestSettingsExit { context.findActivity()?.finish() }
    }
    var pendingImportedSetupId by remember { mutableStateOf<Int?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(externalIcsUri, state.loaded) {
        val uri = externalIcsUri ?: return@LaunchedEffect
        if (!state.loaded) return@LaunchedEffect
        screen = Screen.Home
        pendingHomeAnchoredOverlay = null
        homeAnchoredOverlayRequest = null
        loadExternalIcsFile(context, uri)
            .onSuccess { file ->
                if (!file.isIcs) {
                    Toast.makeText(context, "分享的文件不是有效的 ICS 日历", Toast.LENGTH_SHORT).show()
                    return@onSuccess
                }
                IcsScheduleCodec.parse(file.bytes, state.config)
                    .onSuccess { draft ->
                        homeDialog = HomeDialog.ConfirmImport(draft, returnDialog = null)
                    }
                    .onFailure { error ->
                        Toast.makeText(context, error.message ?: "ICS 解析失败", Toast.LENGTH_SHORT).show()
                    }
            }
            .onFailure { error ->
                Toast.makeText(context, error.message ?: "ICS 文件读取失败", Toast.LENGTH_SHORT).show()
            }
        // Consume only after the load settles; consuming first would clear the URI state and
        // cancel this very effect while the file read is still in flight, silently dropping
        // the shared file.
        onExternalIcsConsumed(uri)
    }
    val backgroundBackdrop = rememberGlassLayerBackdrop(
        domain = GlassBackdropDomain.Background,
        providerId = "home-background",
        sceneState = glassSceneState
    )
    val contentBackdrop = rememberGlassLayerBackdrop(
        domain = GlassBackdropDomain.Content,
        providerId = "home-content",
        sceneState = glassSceneState
    )
    val pickerSceneBackdrop = rememberGlassLayerBackdrop(
        domain = GlassBackdropDomain.PickerScene,
        providerId = "home-picker-scene",
        sceneState = glassSceneState
    )
    val centeredDialogSceneBackdrop = rememberCenteredDialogSceneBackdrop(
        providerId = "home-centered-dialog-scene"
    )
    val chromeBackdrop = rememberGlassCombinedBackdrop(backgroundBackdrop, contentBackdrop)
    remember(glassSceneState) {
        glassSceneState.requireValidTopology(
            nodes = listOf(
                GlassTopologyNode("home-background", GlassBackdropDomain.Background, GlassTopologyNodeRole.Producer),
                GlassTopologyNode("home-content", GlassBackdropDomain.Content, GlassTopologyNodeRole.Producer),
                GlassTopologyNode("home-chrome", GlassBackdropDomain.ChromeCombined, GlassTopologyNodeRole.Consumer),
                GlassTopologyNode("home-picker", GlassBackdropDomain.PickerScene, GlassTopologyNodeRole.Producer),
                GlassTopologyNode("home-dialog", GlassBackdropDomain.DialogBridge, GlassTopologyNodeRole.Consumer),
                GlassTopologyNode("home-cached-week", GlassBackdropDomain.Content, GlassTopologyNodeRole.Producer),
                GlassTopologyNode(
                    "home-personalization",
                    GlassBackdropDomain.ChromeCombined,
                    GlassTopologyNodeRole.Consumer
                )
            ),
            links = listOf(
                GlassSamplingLink("home-background", "home-chrome"),
                GlassSamplingLink("home-content", "home-chrome"),
                GlassSamplingLink("home-background", "home-dialog"),
                GlassSamplingLink("home-content", "home-dialog"),
                GlassSamplingLink("home-cached-week", "home-personalization")
            )
        )
    }
    var homeReadabilityRootSize by remember { mutableStateOf(IntSize.Zero) }
    var homeRootPositionOnScreen by remember { mutableStateOf(Offset.Zero) }
    var homeRootPositionInWindow by remember { mutableStateOf(Offset.Zero) }
    fun currentHomeMenuDestinationRequest(
        kind: HomeMenuDestinationKind
    ): HomeMenuDestinationRequest? {
        val sourceButton = addButtonBounds ?: return null
        if (homeReadabilityRootSize.width <= 0 || homeReadabilityRootSize.height <= 0) return null
        val menuBounds = homeAddMenuBoundsInRoot ?: homeAddMenuTargetRect(
            source = sourceButton,
            rootSize = homeReadabilityRootSize,
            density = density.density,
            actionCount = 6,
            adaptiveMetrics = homeAdaptiveMetrics
        )
        return HomeMenuDestinationRequest(
            kind = kind,
            sourceBoundsInRoot = menuBounds,
            collapseBoundsInRoot = sourceButton
        )
    }
    fun openHomeMenuDestination(kind: HomeMenuDestinationKind) {
        val request = currentHomeMenuDestinationRequest(kind) ?: return
        destinationOwnsButtonReturn = false
        destinationCollapseHandedOff = false
        homeMenuDestinationRequest = request
    }
    val wallpaperImages by rememberHomeWallpaperImages(visualState.config)
    val homeReadabilityContext = remember(
        wallpaperImages.readabilityBitmap,
        visualState.config,
        homeReadabilityRootSize
    ) {
        HomeReadabilityContext(
            bitmap = wallpaperImages.readabilityBitmap,
            config = visualState.config,
            rootSize = homeReadabilityRootSize
        )
    }
    val appDarkTheme = appUsesDarkTheme(visualState.config)
    val statusBarWallpaperLuminance = remember(homeReadabilityContext) {
        homeStatusBarWallpaperLuminance(homeReadabilityContext)
    }
    var homeStatusBarDarkIcons by remember { mutableStateOf(!appDarkTheme) }
    LaunchedEffect(statusBarWallpaperLuminance, appDarkTheme) {
        homeStatusBarDarkIcons = statusBarWallpaperLuminance?.let { luminance ->
            // A small dead band prevents icon polarity from flickering while brightness is dragged.
            if (homeStatusBarDarkIcons) luminance >= 0.46f else luminance >= 0.56f
        } ?: !appDarkTheme
    }
    LaunchedEffect(screen, homeStatusBarDarkIcons, appDarkTheme, context) {
        val window = context.findActivity()?.window ?: return@LaunchedEffect
        window.setStatusBarDarkIcons(
            darkIcons = if (screen is Screen.Home) homeStatusBarDarkIcons else !appDarkTheme
        )
    }
    val expectedWallpaperRenderKey = homeWallpaperRenderKey(
        visualState.config,
        appUsesDarkTheme(visualState.config)
    )
    val wallpaperLoadFinished = !visualState.config.hasAnyWallpaper() ||
        wallpaperImages.renderKey == expectedWallpaperRenderKey
    LaunchedEffect(wallpaperImages.renderKey, expectedWallpaperRenderKey) {
        if (wallpaperImages.renderKey == expectedWallpaperRenderKey) {
            // Keep the cheap GPU blur preview visible until the one final CPU-cached bitmap is
            // ready, then hand over without exposing the previously committed blur for a frame.
            personalizationPreviewState.clear(PersonalizeWallpaperBlurSlider)
        }
    }
    val latestOnStartupContentReady = rememberUpdatedState(onStartupContentReady)
    var startupContentReported by remember { mutableStateOf(false) }
    val latestVisualState = rememberUpdatedState(visualState)
    val latestWallpaperImages = rememberUpdatedState(wallpaperImages)
    val latestAllSchedulesState = rememberUpdatedState(allSchedulesState)
    val startupAnimationsEnabled = remember(context) { animationsEnabled(context) }

    var startupPhase by remember {
        mutableStateOf(if (splashEntranceDone || !startupAnimationsEnabled) StartupPhase.FullQuality else StartupPhase.Loading)
    }
    LaunchedEffect(state.loaded, wallpaperLoadFinished, startupAnimationsEnabled) {
        if (!state.loaded || !wallpaperLoadFinished) return@LaunchedEffect
        if (startupPhase == StartupPhase.Loading && !startupAnimationsEnabled) {
            splashEntranceDone = true
            startupPhase = StartupPhase.FullQuality
        }
        // Let the ready bitmap participate in a complete Compose frame before
        // releasing MainActivity's first-draw gate.
        withFrameNanos { }
        if (!startupContentReported) {
            startupContentReported = true
            latestOnStartupContentReady.value()
        }
        // Show the already-rendered wallpaper for a brief beat, then let the
        // home elements fly in. This keeps the entrance readable without
        // bringing back the old loading mask or circular reveal.
        if (startupPhase == StartupPhase.Loading && startupAnimationsEnabled) {
            delay(140)
            startupPhase = StartupPhase.Entrance
        }
    }
    LaunchedEffect(startupPhase, startupAnimationsEnabled) {
        if (!startupAnimationsEnabled) {
            splashEntranceDone = true
            startupPhase = StartupPhase.FullQuality
            return@LaunchedEffect
        }
        if (startupPhase == StartupPhase.Entrance) {
            splashEntranceDone = false
            delay(820)
            startupPhase = StartupPhase.Settle
            delay(180)
            splashEntranceDone = true
            startupPhase = StartupPhase.FullQuality
        }
    }
    val glassQuality = animatedGlassQuality(startupPhase)
    val adaptiveGlassState = rememberFallbackAdaptiveGlassState(visualState.config)
    val homeAnchoredKindLabel = when (homeAnchoredMorphState.renderedKind) {
        HomeAnchoredOverlayKind.Add -> "AddMenu"
        HomeAnchoredOverlayKind.Personalize -> "Personalize"
        null -> "Unknown"
    }
    val homeMenuDestinationKindLabel = when (homeMenuDestinationMotionState.kind) {
        HomeMenuDestinationKind.AddCourse -> "AddCourse"
        HomeMenuDestinationKind.ManualImport -> "ManualImport"
        null -> "Unknown"
    }
    val startupAnimation = when {
        personalizationSliderPreviewKey != null -> "PersonalizeSliderDrag"
        homeMenuDestinationMotionState.phase != HomeAnchoredOverlayPhase.Idle -> when (homeMenuDestinationMotionState.phase) {
            HomeAnchoredOverlayPhase.Preparing -> "HomeMenuDestinationPrepare:$homeMenuDestinationKindLabel"
            HomeAnchoredOverlayPhase.Opening -> "HomeMenuDestinationOpen:$homeMenuDestinationKindLabel"
            HomeAnchoredOverlayPhase.Closing,
            HomeAnchoredOverlayPhase.Disposing -> "HomeMenuDestinationClose:$homeMenuDestinationKindLabel"
            HomeAnchoredOverlayPhase.Open -> "Idle"
            HomeAnchoredOverlayPhase.Idle -> "Idle"
        }
        homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle -> when (homeAnchoredMorphState.phase) {
            HomeAnchoredOverlayPhase.Preparing -> if (homeAnchoredKindLabel == "Personalize") {
                "PersonalizePrepare"
            } else "HomeAnchoredPrepare:$homeAnchoredKindLabel"
            HomeAnchoredOverlayPhase.Opening -> if (homeAnchoredKindLabel == "Personalize") {
                "PersonalizeOpen"
            } else "HomeAnchoredOpen:$homeAnchoredKindLabel"
            HomeAnchoredOverlayPhase.Closing,
            HomeAnchoredOverlayPhase.Disposing -> if (homeAnchoredKindLabel == "Personalize") {
                "PersonalizeClose"
            } else "HomeAnchoredClose:$homeAnchoredKindLabel"
            HomeAnchoredOverlayPhase.Open -> "Idle"
            HomeAnchoredOverlayPhase.Idle -> "Idle"
        }
        courseEditorOverlayPhase == CourseEditorOverlayPhase.Preparing -> "CourseEditorPrepare"
        courseEditorOverlayPhase == CourseEditorOverlayPhase.Opening -> "CourseEditorOpen"
        courseEditorOverlayPhase == CourseEditorOverlayPhase.Closing ||
            courseEditorOverlayPhase == CourseEditorOverlayPhase.Disposing -> "CourseEditorClose"
        startupPhase == StartupPhase.Reveal -> "StartupReveal"
        startupPhase == StartupPhase.Entrance -> "HomeFlyInEntrance"
        startupPhase == StartupPhase.Settle -> "HomeFlyInEntrance"
        homeDialogVisible -> "DialogOpen"
        else -> "Idle"
    }
    val glassRenderPhase = when {
        startupAnimation.contains("Prepare", ignoreCase = true) -> GlassRenderPhase.Preparing
        startupAnimation.contains("Close", ignoreCase = true) -> GlassRenderPhase.Closing
        startupAnimation != "Idle" -> GlassRenderPhase.Moving
        else -> GlassRenderPhase.Live
    }
    LaunchedEffect(glassRenderPhase) {
        glassSceneState.synchronizePhase(glassRenderPhase)
    }
    val startupEntranceSpec = rememberStartupEntranceSpec(
        phase = startupPhase,
        courseCount = state.courses.size,
        animationsEnabled = startupAnimationsEnabled
    )
    StartupPerformanceBoost(
        startupPhase == StartupPhase.Reveal ||
            startupPhase == StartupPhase.Entrance ||
            startupPhase == StartupPhase.Settle ||
            homeDialogVisible
    )
    PerformanceAnimationState(startupAnimation, startupAnimation != "Idle")
    GlassPerformanceDiagnostics(glassSceneState, startupAnimation)
    RefreshCadenceDiagnostics(
        enabled = BuildConfig.SLEEPDOWN_LARGE_GLASS_EXPERIMENT && startupAnimation != "Idle"
    )
    StartupJankStats(
        phase = startupPhase,
        screen = if (screen is Screen.Home) "Home" else if (screen is Screen.Config) "Settings" else "Other",
        animation = startupAnimation,
        personalizeMode = if (activeHomeAnchoredOverlay == HomeAnchoredOverlayKind.Personalize) {
            homeMode.name
        } else {
            "Idle"
        },
        personalizeSlider = when (personalizationSliderPreviewKey) {
            PersonalizeWallpaperBlurSlider -> "WallpaperBlur"
            PersonalizeWallpaperBrightnessSlider -> "WallpaperBrightness"
            PersonalizeWeekHeightSlider -> "WeekHeight"
            PersonalizeCardAlphaSlider -> "CardAlpha"
            PersonalizeCardBlurSlider -> "CardBlur"
            PersonalizeCardFontSlider -> "CardFont"
            else -> "Idle"
        }
    )
    val reduceWallpaperQualityForCourseEditor =
        courseEditorOverlayPhase == CourseEditorOverlayPhase.Preparing ||
            courseEditorOverlayPhase == CourseEditorOverlayPhase.Opening ||
            (courseEditorRequest != null && courseEditorOverlayPhase == CourseEditorOverlayPhase.Open)
    val courseEditorBackdropBase =
        if (
            reduceWallpaperQualityForCourseEditor ||
            (courseEditorRequest != null && courseEditorOverlayPhase != CourseEditorOverlayPhase.Open)
        ) {
            backgroundBackdrop
        } else {
            chromeBackdrop
        }
    val courseEditorBackdrop = rememberScreenScaledBackdrop(
        backdrop = courseEditorBackdropBase,
        scale = { courseEditorMotionState.backgroundZoom.value },
        rootPositionOnScreen = { homeRootPositionOnScreen },
        rootSize = { homeReadabilityRootSize }
    )
    val weekPersonalizationMotionActive = homeMode == HomeMode.Week &&
        (
            homeAnchoredOverlayRequest?.kind == HomeAnchoredOverlayKind.Personalize ||
                homeAnchoredMorphState.renderedKind == HomeAnchoredOverlayKind.Personalize
            ) && personalizationSliderPreviewKey == null
    val homeAnchoredOverlayBackdrop = rememberScreenScaledBackdrop(
        backdrop = if (weekPersonalizationMotionActive) {
            cachedWeekHomeBackdrop
        } else {
            chromeBackdrop
        },
        scale = {
            val zoom = homeAnchoredMorphState.backgroundZoom.value
            1f + (zoom - 1f) * (1f - personalizationPreviewProgress)
        },
        rootPositionOnScreen = { homeRootPositionOnScreen },
        rootSize = { homeReadabilityRootSize }
    )
    // Android Dialog content owns a separate Compose root/window. Wrap each recorded layer in
    // screen coordinates before combining them, otherwise LayerBackdrop mixes the dialog and
    // activity window origins and samples with a decor/status-bar offset on ColorOS.
    val homeDialogBackgroundBackdrop = rememberScreenScaledBackdrop(
        backdrop = backgroundBackdrop,
        scale = { 1f },
        rootPositionOnScreen = { homeRootPositionOnScreen },
        rootSize = { homeReadabilityRootSize }
    ) ?: backgroundBackdrop
    val homeDialogContentBackdrop = rememberScreenScaledBackdrop(
        backdrop = contentBackdrop,
        scale = { 1f },
        rootPositionOnScreen = { homeRootPositionOnScreen },
        rootSize = { homeReadabilityRootSize }
    ) ?: contentBackdrop
    val homeDialogBackdrop = rememberGlassCombinedBackdrop(
        homeDialogBackgroundBackdrop,
        homeDialogContentBackdrop
    )
    val homeMenuDestinationBackdrop = rememberScreenScaledBackdrop(
        backdrop = chromeBackdrop,
        scale = { homeMenuDestinationMotionState.backgroundZoom.value },
        rootPositionOnScreen = { homeRootPositionOnScreen },
        rootSize = { homeReadabilityRootSize }
    )
    val homeOverlayBackgroundZoom: () -> Float = {
        if (personalizationPreviewProgress > 0.001f) {
            val zoom = homeAnchoredMorphState.backgroundZoom.value
            1f + (zoom - 1f) * (1f - personalizationPreviewProgress)
        } else {
            maxOf(
                courseEditorMotionState.backgroundZoom.value,
                homeAnchoredMorphState.backgroundZoom.value,
                homeMenuDestinationMotionState.backgroundZoom.value
            )
        }
    }
    val systemDark = isSystemInDarkTheme()
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val wallpaperLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            appScope.launch {
                val entrySnapshot = runCatching {
                    captureRenderToken += 1
                    withFrameNanos { }
                    screenGraphicsLayer.toImageBitmap().asAndroidBitmap()
                }.getOrNull()
                homeDialog = HomeDialog.EditWallpaper(uri, entrySnapshot)
            }
        }
    }
    val todayDate = LocalDate.now()
    val homeCurrentWeek = effectiveCurrentWeek(visualState.config)
    val beforeScheduleTerm = isBeforeScheduleTerm(visualState.config, todayDate)
    val afterScheduleTerm = isAfterScheduleTerm(visualState.config, todayDate)
    var homeDisplayWeek by remember(visualState.config.id) { mutableIntStateOf(1) }
    var pendingConflictCourseId by remember(visualState.config.id) { mutableStateOf<Long?>(null) }
    var pendingConflictCourseKey by remember(visualState.config.id) { mutableStateOf<String?>(null) }
    var pendingConflictWeeks by remember(visualState.config.id) { mutableStateOf<List<Int>>(emptyList()) }
    var homeWeekInitialized by remember(visualState.config.id) { mutableStateOf(false) }
    var homeDisplayDate by remember(visualState.config.id) { mutableStateOf(todayDate) }
    LaunchedEffect(visualState.loaded, visualState.config.id, visualState.config.totalWeeks, homeCurrentWeek, visualState.config.autoCurrentWeek, beforeScheduleTerm) {
        if (!visualState.loaded) return@LaunchedEffect
        val currentTargetWeek = if (beforeScheduleTerm) 1 else homeCurrentWeek
        if (!homeWeekInitialized) {
            homeDisplayWeek = currentTargetWeek
            homeWeekInitialized = true
            return@LaunchedEffect
        }
        homeDisplayWeek = homeDisplayWeek.coerceIn(1, visualState.config.totalWeeks.coerceAtLeast(1))
        if (visualState.config.autoCurrentWeek) homeDisplayWeek = currentTargetWeek
    }
    val homeTitleWeek = if (homeMode == HomeMode.Day) {
        effectiveCurrentWeek(visualState.config, homeDisplayDate)
    } else {
        homeDisplayWeek
    }
    val homeTitleDate = if (homeMode == HomeMode.Day) {
        homeDisplayDate
    } else {
        todayDate
    }
    val homeReturnTargetWeek = if (beforeScheduleTerm) 1 else homeCurrentWeek
    // 课程（日视图）按所显示日期是否今天判断，周课表按展示周是否本周判断：
    // 只要滑离当前视图，顶部就提示「点击此处回到本周」。
    val homeShowingAnotherWeek = if (homeMode == HomeMode.Day) {
        homeDisplayDate != todayDate
    } else {
        homeDisplayWeek != homeReturnTargetWeek
    }
    val homeCourseColorSignature = remember(visualState.config.id, visualState.courses) {
        visualState.courses
            .map(::courseCardColorKey)
            .distinct()
            .sorted()
    }
    val homeCoursePalette = remember(
        visualState.config.courseCardColorMode,
        visualState.config.cardColorArgb,
        visualState.config.courseCardPalette,
        wallpaperImages.representativeColors
    ) {
        resolvedCourseCardPalette(visualState.config, wallpaperImages.representativeColors)
    }
    val homeCourseColorAssignments = remember(
        visualState.config.id,
        visualState.config.courseCardColorMode,
        visualState.config.cardColorArgb,
        visualState.config.courseCardPalette,
        homeCourseColorSignature,
        homeCoursePalette
    ) {
        // Card movement and resizing must not recolor the whole grid.
        // The semantic color-key set changes only when the actual course identities do.
        buildCourseCardColorAssignments(
            visualState.courses,
            homeCoursePalette,
            tonalFamily = visualState.config.courseCardColorMode == CourseCardColorMode.GRADIENT
        )
    }
    val homeCaptureFrameKey = remember(
        captureRenderToken,
        visualState.config,
        visualState.courses,
        visualState.periods,
        wallpaperImages.renderKey,
        wallpaperImages.source,
        homeCourseColorSignature,
        homeMode,
        homeDisplayWeek,
        homeDisplayDate,
        editingCourseId,
        activeHomeAnchoredOverlay,
        addButtonHidden,
        homeMenuSourceHidden
    ) {
        buildString {
            append(captureRenderToken).append('|')
            append(visualState.config.hashCode()).append('|')
            append(visualState.courses.hashCode()).append('|')
            append(visualState.periods.hashCode()).append('|')
            append(wallpaperImages.renderKey).append('|')
            append(wallpaperImages.source != null).append('|')
            append(homeCourseColorSignature.hashCode()).append('|')
            append(homeMode).append('|').append(homeDisplayWeek).append('|').append(homeDisplayDate)
                .append('|').append(editingCourseId)
                .append('|').append(activeHomeAnchoredOverlay)
                .append('|').append(addButtonHidden)
                // Source handoff hides the first-level menu without changing the schedule data.
                // Include it in the capture key so the clean background is actually re-recorded
                // after the hidden frame, rather than reusing the menu-containing texture.
                .append('|').append(homeMenuSourceHidden)
        }
    }
    val useFrozenHomeMorphBlur: () -> Boolean = {
        val homeOverlayActive =
            homeAnchoredOverlayRequest != null ||
                homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle ||
                homeMenuDestinationRequest != null ||
                homeMenuDestinationMotionState.phase != HomeAnchoredOverlayPhase.Idle
        val courseEditorActive =
            courseEditorRequest != null || courseEditorOverlayPhase != CourseEditorOverlayPhase.Idle
        shouldUseFrozenHomeMorphBlur(
            screenIsHome = screen is Screen.Home,
            previewActive = personalizationSliderPreviewKey != null ||
                personalizationPreviewProgress > 0.001f,
            overlayActive = homeOverlayActive || courseEditorActive
        )
    }
    val useCachedWeekHomeSurface: () -> Boolean = {
        val homeOverlayActive =
            homeAnchoredOverlayRequest != null ||
                homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle ||
                homeMenuDestinationRequest != null ||
                homeMenuDestinationMotionState.phase != HomeAnchoredOverlayPhase.Idle
        val courseEditorActive =
            courseEditorRequest != null || courseEditorOverlayPhase != CourseEditorOverlayPhase.Idle
        shouldReuseWeekHomeSurface(
            screenIsHome = screen is Screen.Home,
            homeMode = homeMode,
            previewActive = personalizationSliderPreviewKey != null ||
                personalizationPreviewProgress > 0.001f,
            overlayActive = homeOverlayActive || courseEditorActive,
            cachedScheduleId = recordedScheduleId.get(),
            currentScheduleId = visualState.config.id,
            cachedFrameKey = lastRecordedHomeFrameKey.get(),
            currentFrameKey = homeCaptureFrameKey
        )
    }
    val substantialHomeAnchoredCoverage =
        activeHomeAnchoredOverlay == HomeAnchoredOverlayKind.Personalize &&
            (
                homeAnchoredOverlayRequest?.kind == HomeAnchoredOverlayKind.Personalize ||
                    homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle
                )
    val substantialMenuDestinationCoverage =
        homeMenuDestinationRequest != null ||
            homeMenuDestinationMotionState.phase != HomeAnchoredOverlayPhase.Idle
    val substantialCourseEditorCoverage =
        courseEditorRequest != null || courseEditorOverlayPhase != CourseEditorOverlayPhase.Idle
    val substantialOverlaySessionActive =
        substantialHomeAnchoredCoverage ||
            substantialMenuDestinationCoverage ||
            substantialCourseEditorCoverage
    val courseGlassPersonalizationPreviewActive =
        personalizationSliderPreviewKey != null || personalizationPreviewProgress > 0.001f
    val weekCourseGlassOcclusionEligible =
        BuildConfig.SLEEPDOWN_LARGE_GLASS_EXPERIMENT &&
            screen is Screen.Home &&
            homeMode == HomeMode.Week &&
            !courseGlassPersonalizationPreviewActive &&
            substantialOverlaySessionActive
    val weekCourseGlassVisibleMorphActive =
        (substantialHomeAnchoredCoverage &&
            (homeAnchoredMorphState.phase == HomeAnchoredOverlayPhase.Opening ||
                homeAnchoredMorphState.phase == HomeAnchoredOverlayPhase.Open ||
                homeAnchoredMorphState.phase == HomeAnchoredOverlayPhase.Closing)) ||
            homeMenuDestinationMotionState.phase == HomeAnchoredOverlayPhase.Opening ||
            homeMenuDestinationMotionState.phase == HomeAnchoredOverlayPhase.Open ||
            homeMenuDestinationMotionState.phase == HomeAnchoredOverlayPhase.Closing ||
            courseEditorOverlayPhase == CourseEditorOverlayPhase.Opening ||
            courseEditorOverlayPhase == CourseEditorOverlayPhase.Open ||
            courseEditorOverlayPhase == CourseEditorOverlayPhase.Closing
    val homeBackgroundBlurClosing =
        homeAnchoredMorphState.phase == HomeAnchoredOverlayPhase.Closing ||
            homeMenuDestinationMotionState.phase == HomeAnchoredOverlayPhase.Closing ||
            courseEditorOverlayPhase == CourseEditorOverlayPhase.Closing
    val homeOverlayBackgroundBlurProgress: () -> Float = {
        val legacyDepth = homeOverlayDepthProgress(homeOverlayBackgroundZoom())
        val previewActive = personalizationSliderPreviewKey != null ||
            personalizationPreviewProgress > 0.001f
        val substantialOverlayActive = substantialCourseEditorCoverage ||
            substantialMenuDestinationCoverage || substantialHomeAnchoredCoverage
        if (!shouldUseStagedHomeOverlayBlur(previewActive, substantialOverlayActive)) {
            legacyDepth
        } else when {
            substantialCourseEditorCoverage &&
                courseEditorOverlayPhase != CourseEditorOverlayPhase.Idle ->
                stagedHomeOverlayBlurProgress(
                    legacyDepthProgress = legacyDepth,
                    morphProgress = courseEditorMotionState.progress.value,
                    closing = courseEditorOverlayPhase == CourseEditorOverlayPhase.Closing
                )
            substantialMenuDestinationCoverage &&
                homeMenuDestinationMotionState.phase != HomeAnchoredOverlayPhase.Idle ->
                stagedHomeOverlayBlurProgress(
                    legacyDepthProgress = legacyDepth,
                    morphProgress = homeMenuDestinationMotionState.progress.value,
                    closing = homeMenuDestinationMotionState.phase == HomeAnchoredOverlayPhase.Closing
                )
            substantialHomeAnchoredCoverage &&
                homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle ->
                stagedHomeOverlayBlurProgress(
                    legacyDepthProgress = legacyDepth,
                    morphProgress = homeAnchoredMorphState.progress.value,
                    closing = homeAnchoredMorphState.phase == HomeAnchoredOverlayPhase.Closing
                )
            else -> legacyDepth
        }
    }
    LaunchedEffect(
        substantialOverlaySessionActive,
        screen,
        homeMode,
        visualState.config.id,
        homeDisplayWeek,
        homeAdaptiveMetrics.screenWidth,
        homeAdaptiveMetrics.screenHeight,
        courseGlassPersonalizationPreviewActive
    ) {
        val widthDp = homeAdaptiveMetrics.screenWidth.value.roundToInt()
        val heightDp = homeAdaptiveMetrics.screenHeight.value.roundToInt()
        if (substantialOverlaySessionActive) {
            courseGlassMaterialRevealProgress.snapTo(1f)
            lastRecordedHomeFrameKey.set(null)
            if (!weekCourseGlassOcclusionEligible) {
                courseGlassForceCacheReplay = false
                courseGlassRestoredGroupKeys = emptySet()
                courseGlassFrozenRestoreGroupKeys = emptyList()
                courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
                return@LaunchedEffect
            }
            val identityChanged = courseGlassSessionScheduleId >= 0 &&
                (
                    courseGlassSessionScheduleId != visualState.config.id ||
                        courseGlassSessionWeek != homeDisplayWeek ||
                        courseGlassSessionWidthDp != widthDp ||
                        courseGlassSessionHeightDp != heightDp
                    ) && courseGlassOcclusionPhase != CourseGlassOcclusionPhase.Live
            if (identityChanged) {
                courseGlassForceCacheReplay = false
                courseGlassRestoredGroupKeys = emptySet()
                courseGlassFrozenRestoreGroupKeys = emptyList()
                courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
                return@LaunchedEffect
            }
            courseGlassOcclusionGeneration += 1
            courseGlassSessionScheduleId = visualState.config.id
            courseGlassSessionWeek = homeDisplayWeek
            courseGlassSessionWidthDp = widthDp
            courseGlassSessionHeightDp = heightDp
            courseGlassRestoredGroupKeys = emptySet()
            courseGlassFrozenRestoreGroupKeys = emptyList()
            courseGlassForceCacheReplay = false
            courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Preparing
            CourseGlassOcclusionTrace.beginMorph(courseGlassOcclusionGeneration)
            return@LaunchedEffect
        }

        if (courseGlassOcclusionPhase == CourseGlassOcclusionPhase.Preparing) {
            // The request disappeared before Opening. Nothing was suspended, so return directly.
            courseGlassMaterialRevealProgress.snapTo(1f)
            lastRecordedHomeFrameKey.set(null)
            courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
            return@LaunchedEffect
        }
        if (
            courseGlassOcclusionPhase == CourseGlassOcclusionPhase.PostCloseRestore ||
            courseGlassOcclusionPhase == CourseGlassOcclusionPhase.Revealing
        ) {
            // A schedule/week/window/preview key changed while post-close material work was active.
            // Never leave a flat fallback or stale cache latched.
            courseGlassMaterialRevealProgress.snapTo(1f)
            lastRecordedHomeFrameKey.set(null)
            courseGlassForceCacheReplay = false
            courseGlassRestoredGroupKeys = emptySet()
            courseGlassFrozenRestoreGroupKeys = emptyList()
            courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
            return@LaunchedEffect
        }
        if (courseGlassOcclusionPhase != CourseGlassOcclusionPhase.Suspended) {
            return@LaunchedEffect
        }
        val sessionStillMatches =
            screen is Screen.Home &&
                homeMode == HomeMode.Week &&
                visualState.config.id == courseGlassSessionScheduleId &&
                homeDisplayWeek == courseGlassSessionWeek &&
                widthDp == courseGlassSessionWidthDp &&
                heightDp == courseGlassSessionHeightDp
        if (!sessionStillMatches) {
            courseGlassMaterialRevealProgress.snapTo(1f)
            lastRecordedHomeFrameKey.set(null)
            courseGlassForceCacheReplay = false
            courseGlassRestoredGroupKeys = emptySet()
            courseGlassFrozenRestoreGroupKeys = emptyList()
            courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
            return@LaunchedEffect
        }

        // Closing is already complete. Drop the exact Home cache onto a cheap real endpoint first:
        // each course retains its layout, text, input and semantics over a clipped translucent color
        // fill, while every Backdrop/decoration node remains absent.
        courseGlassMaterialRevealProgress.snapTo(0f)
        courseGlassOcclusionPhase = CourseGlassOcclusionPhase.PostCloseRestore
        courseGlassForceCacheReplay = false
        withFrameNanos { }

        // A course-editor save can legitimately change the card set while the exact cache is in
        // control. Publish that one post-close topology now, then restore every resulting group at
        // an equivalent 60 Hz cadence behind the opaque-enough flat fallback.
        val groups = courseGlassRestoreRegistry.orderedGroupKeys(homeDisplayWeek)
            .ifEmpty { courseGlassFrozenRestoreGroupKeys }
        courseGlassFrozenRestoreGroupKeys = groups
        var restored = emptySet<String>()
        var previousRestoreTimestamp = 0L
        groups.forEachIndexed { index, groupKey ->
            if (index > 0) {
                while (true) {
                    val frameTimestamp = withFrameNanos { it }
                    if (frameTimestamp - previousRestoreTimestamp >= CourseGlassRestoreCadenceNanos) {
                        previousRestoreTimestamp = frameTimestamp
                        break
                    }
                }
            } else {
                previousRestoreTimestamp = withFrameNanos { it }
            }
            restored = restored + groupKey
            courseGlassRestoredGroupKeys = restored
            CourseGlassOcclusionTrace.recordPostCloseRestoreFrame()
        }

        // All material nodes now exist at alpha zero. Crossfade only their sampled surface and
        // decoration over the stable flat cards; text/layout never participate in this animation.
        courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Revealing
        withFrameNanos { }
        courseGlassMaterialRevealProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = CourseGlassMaterialRevealDurationMillis,
                easing = CubicBezierEasing(0.16f, 0.84f, 0.24f, 1f)
            )
        )
        courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
        lastRecordedHomeFrameKey.set(null)
        courseGlassRestoredGroupKeys = emptySet()
        courseGlassFrozenRestoreGroupKeys = emptyList()
    }

    suspend fun awaitCourseGlassOpeningGate(routeEligible: Boolean) {
        if (
            !routeEligible ||
            !weekCourseGlassOcclusionEligible ||
            courseGlassOcclusionPhase != CourseGlassOcclusionPhase.Preparing
        ) return
        val generation = courseGlassOcclusionGeneration
        var exactCacheSeenOnPreviousFrame = useCachedWeekHomeSurface() &&
            weekHomeSurfaceUsesOffscreenCache.get()
        var confirmed = false
        for (attempt in 0 until 2) {
            withFrameNanos { }
            val exactCacheActive = useCachedWeekHomeSurface() &&
                weekHomeSurfaceUsesOffscreenCache.get()
            if (exactCacheActive && exactCacheSeenOnPreviousFrame) {
                confirmed = true
                break
            }
            exactCacheSeenOnPreviousFrame = exactCacheActive
        }
        if (
            !confirmed ||
            generation != courseGlassOcclusionGeneration ||
            !substantialOverlaySessionActive
        ) {
            courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
            return
        }
        val shouldSuspend = shouldSuspendCourseGlassMaterials(
            experimentEnabled = BuildConfig.SLEEPDOWN_LARGE_GLASS_EXPERIMENT,
            weekMode = homeMode == HomeMode.Week,
            exactCacheCoverActive = true,
            substantialOverlayActive = true
        )
        if (!shouldSuspend) {
            courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
            return
        }
        courseGlassFrozenRestoreGroupKeys =
            courseGlassRestoreRegistry.orderedGroupKeys(homeDisplayWeek)
        courseGlassRestoredGroupKeys = emptySet()
        courseGlassMaterialRevealProgress.snapTo(0f)
        courseGlassForceCacheReplay = true
        courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Suspended
        val flatCacheRequest = courseGlassFlatCacheDrawRequest + 1
        courseGlassFlatCacheDrawRequest = flatCacheRequest
        var flatCacheRecorded = false
        for (attempt in 0 until 2) {
            withFrameNanos { }
            if (courseGlassFlatCacheRecordedRequest.get() >= flatCacheRequest) {
                flatCacheRecorded = true
                break
            }
        }
        if (
            !flatCacheRecorded ||
            generation != courseGlassOcclusionGeneration ||
            !substantialOverlaySessionActive
        ) {
            courseGlassMaterialRevealProgress.snapTo(1f)
            courseGlassForceCacheReplay = false
            courseGlassRestoredGroupKeys = emptySet()
            courseGlassFrozenRestoreGroupKeys = emptyList()
            courseGlassOcclusionPhase = CourseGlassOcclusionPhase.Live
            lastRecordedHomeFrameKey.set(null)
        }
    }

    val effectiveCourseGlassOcclusionPhase = courseGlassOcclusionPhase

    suspend fun awaitRenderedSchedule(scheduleId: Int): Boolean {
        val generationBeforeSwitch = recordedHomeGeneration.get()
        Log.d("SchedulePicker", "await start requested=$scheduleId recorded=${recordedScheduleId.get()} generation=$generationBeforeSwitch")
        previewScheduleId = scheduleId
        captureRenderToken += 1
        snapshotFlow { latestVisualState.value.config.id }.first { it == scheduleId }
        Log.d("SchedulePicker", "visual state ready requested=$scheduleId token=$captureRenderToken")
        val target = latestAllSchedulesState.value.allConfigs.firstOrNull { it.id == scheduleId }
            ?: defaultConfig(scheduleId)
        if (target.hasAnyWallpaper()) {
            // The wallpaper loader currently exposes no terminal error state. Bound this wait
            // so an unreadable URI degrades to the same loading/fallback layer as the real home.
            withTimeoutOrNull(2_500L) {
                snapshotFlow {
                    latestVisualState.value.config.id to (latestWallpaperImages.value.source != null)
                }.first { (renderedId, wallpaperReady) -> renderedId == scheduleId && wallpaperReady }
            }
        }
        val ready = withTimeoutOrNull(900L) {
            while (
                recordedScheduleId.get() != scheduleId ||
                recordedHomeGeneration.get() == generationBeforeSwitch
            ) {
                withFrameNanos { }
            }
            // The marker is written after graphicsLayer.record completes. Cross one more
            // frame boundary so capture never races the frame that produced the marker.
            withFrameNanos { }
            true
        } ?: false
        Log.d(
            "SchedulePicker",
            "await end requested=$scheduleId ready=$ready recorded=${recordedScheduleId.get()} generation=${recordedHomeGeneration.get()}"
        )
        return ready
    }

    suspend fun captureRenderedSchedule(scheduleId: Int): Bitmap? {
        if (!awaitRenderedSchedule(scheduleId)) return null
        val bitmap = runCatching { screenGraphicsLayer.toImageBitmap().asAndroidBitmap() }
            .onFailure { Log.e("SchedulePicker", "capture failed for schedule=$scheduleId", it) }
            .getOrNull()
        Log.d("SchedulePicker", "capture result requested=$scheduleId bitmap=${bitmap?.width}x${bitmap?.height}")
        if (bitmap != null) {
            appScope.launch { ScheduleSnapshotStore.save(context, scheduleId, bitmap) }
        }
        return bitmap
    }

    fun hydratePersistedSnapshots(generation: Int) {
        cacheHydrationJob?.cancel()
        cacheHydrationJob = appScope.launch {
            val selectedId = pickerState.selectedScheduleId ?: return@launch
            val selectedIndex = pickerState.orderIds.indexOf(selectedId)
            val neighborIds = listOf(selectedIndex - 1, selectedIndex + 1)
                .mapNotNull(pickerState.orderIds::getOrNull)
            val orderedTargets = (neighborIds + pickerState.orderIds)
                .distinct()
                .filter { it != selectedId && pickerState.snapshots[it] == null }
            for (targetId in orderedTargets) {
                if (generation != snapshotGeneration || !pickerState.overlayVisible) break
                ScheduleSnapshotStore.load(context, targetId)?.let { persisted ->
                    pickerState.snapshots[targetId] = persisted
                }
            }
        }
    }

    fun prewarmCurrentScheduleSnapshot() {
        entryPrewarmJob?.cancel()
        val requestedId = state.config.id
        entryPrewarmJob = appScope.launch {
            val snapshot = captureRenderedSchedule(requestedId) ?: return@launch
            if (
                pickerState.phase is CustomizeUiState.ShowingEntryButton &&
                state.config.id == requestedId
            ) {
                pickerState.currentSnapshot = snapshot
                pickerState.currentSnapshotScheduleId = requestedId
                pickerState.snapshots[requestedId] = snapshot
            }
        }
    }

    fun enterCustomizePage() {
        if (pickerState.phase !is CustomizeUiState.Home && pickerState.phase !is CustomizeUiState.ShowingEntryButton) return
        snapshotJob?.cancel()
        val generation = ++snapshotGeneration
        appScope.launch {
            showScheduleEntryPill = false
            pickerState.phase = CustomizeUiState.CapturingSnapshots
            val entryExitStartedAt = withFrameNanos { it }
            var frameTime: Long
            do {
                frameTime = withFrameNanos { it }
            } while (frameTime - entryExitStartedAt < 120_000_000L)
            val currentId = state.config.id
            pickerState.selectedScheduleId = currentId
            pickerState.originalScheduleId = currentId
            pickerState.orderIds.clear()
            pickerState.orderIds += currentId
            pickerState.orderIds += allSchedulesState.schedules.map { it.id }.filter { it != currentId }
            val prewarmed = pickerState.currentSnapshot
                ?.takeIf { pickerState.currentSnapshotScheduleId == currentId }
            val recordedNow = if (prewarmed == null && recordedScheduleId.get() == currentId) {
                runCatching { screenGraphicsLayer.toImageBitmap().asAndroidBitmap() }.getOrNull()
            } else null
            val currentSnapshot = prewarmed
                ?: recordedNow
                ?: ScheduleSnapshotStore.load(context, currentId)
                ?: ScheduleSnapshotStore.createEmptySchedulePlaceholder(
                    context,
                    windowContainerSize.width,
                    windowContainerSize.height,
                    if (state.config.followSystemDarkMode) systemDark else state.config.darkMode
                )
            pickerState.currentSnapshot = currentSnapshot
            pickerState.snapshots[currentId] = currentSnapshot
            pickerState.currentSnapshotScheduleId = currentId
            if (recordedNow != null) {
                appScope.launch { ScheduleSnapshotStore.save(context, currentId, recordedNow) }
            }
            pickerState.enterProgress.snapTo(0f)
            pickerState.chromeProgress.snapTo(0f)
            pickerState.pageSpacingProgress.snapTo(0f)
            pickerState.cornerProgress.snapTo(0f)
            pickerState.realHomeRevealProgress.snapTo(0f)
            pickerState.phase = CustomizeUiState.EnteringPicker
            withFrameNanos { }
            coroutineScope {
                launch { pickerState.enterProgress.animateTo(1f, tween(450, easing = CubicBezierEasing(0.3f, 0.72f, 0.2f, 1f))) }
                launch { pickerState.cornerProgress.animateTo(1f, tween(450, easing = CubicBezierEasing(0.3f, 0.72f, 0.2f, 1f))) }
                launch {
                    val titleDelayStart = withFrameNanos { it }
                    var titleFrameTime: Long
                    do {
                        titleFrameTime = withFrameNanos { it }
                    } while (titleFrameTime - titleDelayStart < 100_000_000L)
                    pickerState.chromeProgress.animateTo(1f, tween(350))
                }
                launch { pickerState.pageSpacingProgress.animateTo(1f, tween(500)) }
            }
            if (generation != snapshotGeneration) return@launch
            pickerState.phase = CustomizeUiState.Picker
            hydratePersistedSnapshots(generation)
        }
    }

    fun switchPickerSchedule(scheduleId: Int) {
        if (pickerState.phase !is CustomizeUiState.Picker || scheduleId == pickerState.selectedScheduleId) return
        snapshotJob?.cancel()
        val generation = ++snapshotGeneration
        pickerState.selectedScheduleId = scheduleId
        pickerState.snapshots[scheduleId]?.let {
            pickerState.currentSnapshot = it
            pickerState.currentSnapshotScheduleId = scheduleId
        }
        snapshotJob = appScope.launch {
            val requestedId = scheduleId
            val cached = pickerState.snapshots[requestedId]
                ?: ScheduleSnapshotStore.load(context, requestedId)
            if (cached != null && generation == snapshotGeneration && pickerState.selectedScheduleId == requestedId) {
                pickerState.snapshots[requestedId] = cached
                pickerState.currentSnapshot = cached
                pickerState.currentSnapshotScheduleId = requestedId
            }
            // Pager navigation is visual-only. Do not switch or recapture the hidden real
            // homepage here; doing so rebuilt the entire schedule on every settled swipe and
            // competed with the Pager animation for both main-thread and GPU time.
            if (generation == snapshotGeneration && pickerState.selectedScheduleId == requestedId) {
                hydratePersistedSnapshots(generation)
            }
        }
    }

    fun quickDraftFor(scheduleId: Int): QuickScheduleDraft {
        val config = latestAllSchedulesState.value.allConfigs.firstOrNull { it.id == scheduleId }
            ?: defaultConfig(scheduleId)
        val totalWeeks = config.totalWeeks.coerceIn(1, 60)
        val resolvedWeek = resolveScheduleCurrentWeek(
            config,
            totalWeeks,
            config.currentWeek,
            config.termStartDate,
            config.autoCurrentWeek
        )
        return QuickScheduleDraft(
            scheduleId = scheduleId,
            totalWeeks = totalWeeks,
            currentWeek = resolvedWeek,
            autoCurrentWeek = config.autoCurrentWeek,
            hideEmptyWeekends = config.hideEmptyWeekends,
            termStartDate = config.termStartDate.orEmpty()
        )
    }

    LaunchedEffect(pendingImportedSetupId) {
        val scheduleId = pendingImportedSetupId ?: return@LaunchedEffect
        screen = Screen.Home
        dismissHomeDialog()
        snapshotFlow {
            val latest = latestAllSchedulesState.value
            latest.schedules.any { it.id == scheduleId } &&
                latest.allConfigs.any { it.id == scheduleId } &&
                latestVisualState.value.config.id == scheduleId
        }.first { it }
        if (pickerState.overlayVisible) pickerState.reset()
        withFrameNanos { }
        enterCustomizePage()
        snapshotFlow {
            pickerState.phase is CustomizeUiState.Picker &&
                pickerState.selectedScheduleId == scheduleId
        }.first { it }
        quickScheduleDraft = quickDraftFor(scheduleId)
        pendingImportedSetupId = null
    }

    fun exitPicker(
        apply: Boolean,
        targetOverride: Int? = null,
        commitTarget: Boolean = apply,
        crossfadeToTarget: Boolean = !apply,
        onFinished: (() -> Unit)? = null
    ) {
        if (pickerState.interactionsLocked) return
        snapshotJob?.cancel()
        ++snapshotGeneration
        val targetId = targetOverride
            ?: if (apply) pickerState.selectedScheduleId ?: state.config.id else state.config.id
        appScope.launch {
            // Always start from exactly what the centered card is currently showing, even when
            // Cancel is returning to a different, previously-applied schedule.
            val centeredId = pickerState.selectedScheduleId
            val cachedCardSnapshot = centeredId?.let { id ->
                pickerState.currentSnapshot
                    ?.takeIf { pickerState.currentSnapshotScheduleId == id }
                    ?: pickerState.snapshots[id]
            }
            pickerState.phase = if (commitTarget) CustomizeUiState.Applying else CustomizeUiState.ExitingPicker
            pickerState.preparingExit = true
            val cancelTargetSnapshot = if (crossfadeToTarget) {
                pickerState.snapshots[targetId] ?: ScheduleSnapshotStore.load(context, targetId)
            } else {
                null
            }
            // Prepare the actual live destination, but do not turn it into the second visual
            // layer. The cached centered card will later fade away to reveal this real home.
            awaitRenderedSchedule(targetId)
            pickerState.currentSnapshot = cachedCardSnapshot ?: pickerState.currentSnapshot
            // Cancel visually dissolves the original/applied schedule's persisted card directly
            // over the currently centered card while it grows. Apply continues to enlarge the
            // selected card and hands it directly to the live destination.
            pickerState.transitionFromSnapshot = cancelTargetSnapshot
            pickerState.snapshotCoverBitmap = null
            if (commitTarget) {
                val activationFinished = kotlinx.coroutines.CompletableDeferred<Unit>()
                viewModel.activateSchedule(targetId) { activationFinished.complete(Unit) }
                activationFinished.await()
                withTimeoutOrNull(1_200L) {
                    snapshotFlow { latestVisualState.value.config.id }.first { it == targetId }
                }
            }
            // Force the fully loaded destination through two additional draw/vsync boundaries.
            // The loading indicator remains visible throughout this preparation window.
            captureRenderToken += 1
            withFrameNanos { }
            withFrameNanos { }
            pickerState.preparingExit = false
            coroutineScope {
                launch { pickerState.enterProgress.animateTo(0f, tween(420, easing = CubicBezierEasing(0.3f, 0.72f, 0.2f, 1f))) }
                launch { pickerState.chromeProgress.animateTo(0f, tween(220)) }
                launch { pickerState.pageSpacingProgress.animateTo(0f, tween(320)) }
            }
            // Geometry is now fully screen-sized. Only now flatten the corner radius so no
            // square card edge can be exposed while the card is still floating over the home.
            pickerState.cornerProgress.animateTo(0f, tween(90))
            // The cached card is now fully screen-sized and square. Reveal the already-rendered
            // real home underneath instead of crossfading to another bitmap.
            pickerState.realHomeRevealProgress.animateTo(1f, tween(250))
            val handoffHoldStart = withFrameNanos { it }
            var handoffFrame: Long
            do {
                handoffFrame = withFrameNanos { it }
            } while (handoffFrame - handoffHoldStart < 64_000_000L)
            val temporaryToDelete = pickerState.temporaryIds.filter { !commitTarget || it != targetId }
            previewScheduleId = null
            pickerState.reset()
            temporaryToDelete.forEach(viewModel::deleteSchedule)
            withFrameNanos { }
            onFinished?.invoke()
        }
    }

    val latestPickerBackAction by rememberUpdatedState(newValue = {
        if (pickerState.phase is CustomizeUiState.Picker) {
            if (pickerState.deletingScheduleId != null) {
                pickerState.deletingScheduleId = null
                pickerState.deleteReveal = 0f
            } else {
                // System Back is deliberately identical to the visible Cancel action.
                exitPicker(apply = false)
            }
        }
    })
    BackHandler(enabled = pickerState.overlayVisible) {
        latestPickerBackAction()
    }
    val returnHomeToCurrentDateAndWeek = {
        if (beforeScheduleTerm) {
            homeDisplayDate = parseScheduleDate(visualState.config.termStartDate) ?: LocalDate.now()
            homeDisplayWeek = 1
        } else {
            homeDisplayDate = LocalDate.now()
            homeDisplayWeek = homeCurrentWeek
        }
    }

    LaunchedEffect(state.config.hideFromRecents) {
        hideFromRecentsEnabled = state.config.hideFromRecents
    }

    var initialLifecycleStartSeen by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (initialLifecycleStartSeen) {
                    viewModel.refreshNotifications()
                    pendingPickerEditorScheduleId?.let { scheduleId ->
                        pendingPickerEditorScheduleId = null
                        val generation = ++snapshotGeneration
                        appScope.launch {
                            pickerState.phase = CustomizeUiState.ExitingEditor
                            val snap = captureRenderedSchedule(scheduleId)
                            if (generation == snapshotGeneration && pickerState.selectedScheduleId == scheduleId) {
                                if (snap != null) {
                                    pickerState.snapshots[scheduleId] = snap
                                    pickerState.currentSnapshot = snap
                                    pickerState.currentSnapshotScheduleId = scheduleId
                                }
                                pickerState.phase = CustomizeUiState.Picker
                                hydratePersistedSnapshots(generation)
                            }
                        }
                    }
                } else {
                    initialLifecycleStartSeen = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(message) {
        if (message != null) {
            delay(1800)
            viewModel.clearSnackbar()
        }
    }
    // Root wrapper — ensures splash covers all content including Scaffold internals
    @OptIn(ExperimentalSharedTransitionApi::class)
    SharedTransitionLayout(
        modifier = Modifier.fillMaxSize()
    ) {
    val sharedTransitionScope = this
    val activeSharedTransitionScope = if (startupPhase == StartupPhase.FullQuality) sharedTransitionScope else null
    CompositionLocalProvider(
        LocalSharedTransitionScope provides activeSharedTransitionScope,
        LocalEditingCourseId provides editingCourseId,
        LocalStartupPhase provides startupPhase,
        LocalGlassQuality provides glassQuality,
        LocalStartupEntranceSpec provides startupEntranceSpec,
        LocalAdaptiveGlass provides adaptiveGlassState,
        LocalHomeReadability provides homeReadabilityContext,
        LocalCourseCardPalette provides homeCoursePalette,
        LocalCourseCardColorAssignments provides homeCourseColorAssignments,
        LocalGlassSceneState provides glassSceneState,
        LocalCourseGlassRestoreRegistry provides courseGlassRestoreRegistry,
        LocalCourseGlassMaterialRevealProgress provides courseGlassMaterialRevealProgressProvider,
        LocalCenteredDialogSceneBackdrop provides centeredDialogSceneBackdrop
    ) {
    // Home now owns an explicit Miuix root scaffold, matching the already-fixed secondary-page
    // topology. Its complete business stack is the underlay; Miuix renders every second-level
    // popup in the scaffold's later sibling host, so a dialog consumer can never be recorded by
    // the LayerBackdrop it samples.
    top.yukonga.miuix.kmp.basic.Scaffold(
        modifier = Modifier.fillMaxSize(),
        underlayModifier = Modifier
            .fillMaxSize()
            .centeredDialogSceneProducer(centeredDialogSceneBackdrop),
        popupHost = {
            Box(Modifier.fillMaxSize()) {
                // Overlay content is subcomposed by the host, not at its call site. Preserve the
                // same Miuix/Glass CompositionLocals that wrapped the 1.1.5 host; otherwise sheet
                // rows fall back to the taller non-Miuix implementation and their dividers become
                // visible. The host remains a sibling of the backdrop producer, so this does not
                // reintroduce the former producer/consumer recursion.
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            if (detailPopupCaptureActive) {
                                val requestedToken = detailPopupCaptureToken
                                if (detailPopupCapturedToken.get() != requestedToken) {
                                    detailPopupGraphicsLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    detailPopupCapturedToken.set(requestedToken)
                                }
                                drawLayer(detailPopupGraphicsLayer)
                            } else {
                                drawContent()
                            }
                        }
                ) {
                    GlassMiuixSettingsTheme(settingsVisualConfig(state.config)) {
                        MiuixPopupHost()
                    }
                }

                // Detailed settings deliberately overlays the still-mounted quick sheet, matching
                // the 1.2.0 handoff. Keeping it after the Miuix host prevents the retained sheet
                // from covering the destination without closing/reopening either surface.
                detailCaptureCoverBitmap?.let { cover ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .zIndex(259f)
                            .drawWithContent {
                                if (!detailCaptureMaskActive.get()) drawContent()
                            }
                            .pointerInput(cover) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent().changes.forEach { it.consume() }
                                    }
                                }
                            }
                    ) {
                        Image(
                            bitmap = cover.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                detailMorphRequest?.let { request ->
                    val detailState = allSchedulesState.forSchedule(request.scheduleId)
                    DetailScheduleMorphOverlay(
                        request = request,
                        detailState = detailState,
                        onMorphStateChange = { detailMorphState = it },
                        onSave = { config, periods ->
                            viewModel.saveConfigForSchedule(request.scheduleId, config, periods)
                        },
                        onFinished = {
                            detailMorphRequest = null
                            detailCaptureCoverBitmap = null
                            detailCaptureRecordCleanFrame = false
                            detailPopupCaptureActive = false
                            detailMorphState = DetailMorphState.Idle
                        },
                        modifier = Modifier.zIndex(260f)
                    )
                }
            }
        },
        containerColor = ComposeColor.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true }
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned {
                homeReadabilityRootSize = it.size
                homeRootPositionOnScreen = it.positionOnScreen()
                homeRootPositionInWindow = it.positionInWindow()
            }
            .drawWithContent {
                val recordCleanFrame =
                    detailMorphState is DetailMorphState.Capturing && detailCaptureRecordCleanFrame
                val courseEditorOwnsFrame =
                    courseEditorRequest != null ||
                        courseEditorOverlayPhase != CourseEditorOverlayPhase.Idle
                // Never record the course editor into the detail-page capture layer. It contains
                // several backdrop consumers and recording that entire overlay on every animation
                // frame causes an explosive offscreen-render workload after the first use.
                // HomeAnchored/HomeMenuDestination own similarly heavy live backdrop consumers;
                // pause this unrelated capture while they move. Their settled Open frame remains
                // available to flows such as manual-import history detail background capture.
                // Keep the detailed-settings source frame live while the quick sheet animates.
                // The detailed-settings handoff needs consecutive fully composed source and
                // clean-background frames, as in the 1.1.5 implementation.
                val shouldRecordStableDetailFrame =
                    detailMorphState is DetailMorphState.Idle &&
                        !courseEditorOwnsFrame &&
                        (
                            homeAnchoredMorphState.phase == HomeAnchoredOverlayPhase.Open ||
                                (homeAnchoredOverlayRequest == null &&
                                    homeAnchoredMorphState.phase == HomeAnchoredOverlayPhase.Idle)
                            ) &&
                        (
                            homeMenuDestinationMotionState.phase == HomeAnchoredOverlayPhase.Open ||
                                (homeMenuDestinationRequest == null &&
                                    homeMenuDestinationMotionState.phase == HomeAnchoredOverlayPhase.Idle)
                            )
                if (shouldRecordStableDetailFrame || recordCleanFrame) {
                    detailCaptureMaskActive.set(recordCleanFrame)
                    try {
                        detailScreenGraphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                    } finally {
                        detailCaptureMaskActive.set(false)
                    }
                }
                drawContent()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
        // The live home surface owns shared depth for source-anchored overlays. Consumers render
        // outside this layer and compensate through rememberScreenScaledBackdrop, keeping the
        // liquid sampling coordinates aligned without blurring the foreground panel itself.
        HomeBackgroundZoomLayer(
            zoom = homeOverlayBackgroundZoom,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
        HomeBackgroundBlurLayer(
            blurProgress = homeOverlayBackgroundBlurProgress,
            useFrozenHomeScene = useFrozenHomeMorphBlur,
            closing = { homeBackgroundBlurClosing },
            sceneKey = homeCaptureFrameKey,
            modifier = Modifier.fillMaxSize()
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    val requestedFlatCache = courseGlassFlatCacheDrawRequest
                    val cachedHomeActive = useCachedWeekHomeSurface() ||
                        (courseGlassForceCacheReplay &&
                            weekHomeSurfaceUsesOffscreenCache.get())
                    val shouldRecordFlatCache =
                        courseGlassOcclusionPhase == CourseGlassOcclusionPhase.Suspended &&
                            courseGlassForceCacheReplay &&
                            requestedFlatCache > courseGlassFlatCacheRecordedRequest.get()
                    if (shouldRecordFlatCache) {
                        // Opening still replays one neutral Home GPU layer, but replace its course
                        // pixels once with the real flat fallback before the first visible morph
                        // frame. No course Backdrop/decoration node exists in this recording.
                        screenGraphicsLayer.alpha = 1f
                        screenGraphicsLayer.record { this@drawWithContent.drawContent() }
                        recordedScheduleId.set(visualState.config.id)
                        recordedHomeGeneration.incrementAndGet()
                        lastRecordedHomeFrameKey.set(homeCaptureFrameKey)
                        courseGlassFlatCacheRecordedRequest.set(requestedFlatCache)
                        if (weekHomeSurfaceUsesOffscreenCache.compareAndSet(false, true)) {
                            screenGraphicsLayer.compositingStrategy =
                                androidx.compose.ui.graphics.layer.CompositingStrategy.Offscreen
                        }
                        drawLayer(screenGraphicsLayer)
                    } else if (cachedHomeActive) {
                        screenGraphicsLayer.alpha = 1f
                        if (weekHomeSurfaceUsesOffscreenCache.compareAndSet(false, true)) {
                            // The recorded display list contains every week-course Kyant node.
                            // Flatten it only for popup motion so those child RenderNodes stop
                            // participating in every blurred/zoomed frame. The original live tree
                            // remains composed and resumes after the transition, avoiding a costly
                            // dispose/recreate cycle and preserving every glass pixel.
                            screenGraphicsLayer.compositingStrategy =
                                androidx.compose.ui.graphics.layer.CompositingStrategy.Offscreen
                        }
                        // Replay the already-recorded GPU scene while the outer depth layers and
                        // the real overlay animate. Recording below those depth layers keeps the
                        // cache neutral, so a later live-preview handoff cannot double its zoom.
                        drawLayer(screenGraphicsLayer)
                    } else {
                        screenGraphicsLayer.alpha = 1f
                        if (lastRecordedHomeFrameKey.get() != homeCaptureFrameKey) {
                            // Source-card and top-bar source visibility are part of the key. The
                            // first preparation frame records a clean home with the real source
                            // hidden, so the cached background cannot duplicate the moving clone.
                            if (weekCourseGlassVisibleMorphActive) {
                                CourseGlassOcclusionTrace.recordFullTreeRecordDuringMorph()
                            }
                            screenGraphicsLayer.record { this@drawWithContent.drawContent() }
                            recordedScheduleId.set(visualState.config.id)
                            recordedHomeGeneration.incrementAndGet()
                            lastRecordedHomeFrameKey.set(homeCaptureFrameKey)
                        }
                        if (useCachedWeekHomeSurface()) {
                            // A source handoff can change the frame key in the middle of Opening or
                            // Closing. The old path recorded the complete week scene and then drew
                            // that heavy tree live a second time before using the cache next frame.
                            // Replay the just-recorded, pixel-identical layer immediately instead;
                            // keeping Offscreen throughout also avoids an Auto -> Offscreen round
                            // trip at the small-button handoff.
                            if (weekHomeSurfaceUsesOffscreenCache.compareAndSet(false, true)) {
                                screenGraphicsLayer.compositingStrategy =
                                    androidx.compose.ui.graphics.layer.CompositingStrategy.Offscreen
                            }
                            drawLayer(screenGraphicsLayer)
                        } else {
                            if (weekHomeSurfaceUsesOffscreenCache.compareAndSet(true, false)) {
                                screenGraphicsLayer.compositingStrategy =
                                    androidx.compose.ui.graphics.layer.CompositingStrategy.Auto
                            }
                            if (weekCourseGlassVisibleMorphActive) {
                                CourseGlassOcclusionTrace.recordLiveDrawDuringMorph()
                            }
                            drawContent()
                        }
                    }
                }
        ) {
        Scaffold(
            containerColor = ComposeColor.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (screen !is Screen.Config) TopBarEntranceContainer(
                    phase = startupPhase,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rootTopBarLayoutHeight(screen))
                ) {
                    if (screen is Screen.Home) {
                        AnimatedVisibility(
                            visible = screen is Screen.Config || homeContentUnderTopBar,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .zIndex(10f),
                            enter = fadeIn(animationSpec = spring(dampingRatio = 0.9f, stiffness = 520f)),
                            exit = fadeOut(animationSpec = spring(dampingRatio = 0.95f, stiffness = 560f))
                        ) {
                            HomeTopGradientBlur(
                                config = visualState.config,
                                backdrop = chromeBackdrop,
                                height = rootTopGradientHeight(screen),
                                modifier = Modifier
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(11f)
                    ) {
                        AppTopBar(
                            screen = screen,
                            state = if (screen is Screen.Home) visualState else state,
                            settingsPage = SettingsPage.Root,
                            backdrop = chromeBackdrop,
                            homeDisplayDate = homeTitleDate,
                            homeDisplayWeek = homeTitleWeek,
                            beforeScheduleTerm = beforeScheduleTerm,
                            afterScheduleTerm = afterScheduleTerm,
                            homeShowingAnotherWeek = homeShowingAnotherWeek,
                            onReturnHomeToCurrentWeek = returnHomeToCurrentDateAndWeek,
                            activeHomeOverlay = activeHomeAnchoredOverlay,
                            addButtonHidden = addButtonHidden,
                            onAddButtonPositioned = { addButtonBounds = it },
                            onPersonalizeButtonPositioned = { personalizeButtonBounds = it },
                            onToggleAddMenu = { sourceScale ->
                                toggleHomeAnchoredOverlay(HomeAnchoredOverlayKind.Add, sourceScale)
                            },
                            onTogglePersonalize = { sourceScale ->
                                toggleHomeAnchoredOverlay(HomeAnchoredOverlayKind.Personalize, sourceScale)
                            },
                            onBackHome = { screen = Screen.Home }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .glassBackdropProducer(backgroundBackdrop)
                ) {
                    if (screen is Screen.Home) {
                        if (!visualState.loaded) {
                            HomeBackdropFallback()
                        } else {
                            if (wallpaperImages.source != null) {
                                HomeWallpaper(
                                    visualState.config,
                                    wallpaperImages,
                                    startupPhase,
                                    reduceQuality = reduceWallpaperQualityForCourseEditor,
                                    previewState = personalizationPreviewState
                                )
                                WallpaperGlassSamplingToneOverlay(
                                    visualState.config,
                                    personalizationPreviewState
                                )
                            } else {
                                HomeBackdropFallback()
                            }
                        }
                    } else if (screen is Screen.Config) {
                        Box(Modifier.fillMaxSize().background(settingsPageBackground(settingsVisualConfig(state.config))))
                    } else {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                    }
                }
                /*
                 * Wallpaper brightness is a visual treatment for the wallpaper only. Keep it
                 * outside backgroundBackdrop's provider node: liquid course cards should sample
                 * the original wallpaper, otherwise lowering wallpaper brightness also darkens
                 * every glass surface that consumes this backdrop.
                 */
                if (
                    screen is Screen.Home &&
                    visualState.loaded &&
                    visualState.config.hasAnyWallpaper() &&
                    wallpaperImages.source != null
                ) {
                    WallpaperToneOverlay(visualState.config, personalizationPreviewState)
                }
                val contentModifier = Modifier
                    .fillMaxSize()
                    .glassBackdropProducer(contentBackdrop)
                Column(modifier = contentModifier) {
                    if (screen !is Screen.Home) {
                        message?.let { Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
                    }
                    ContentEntranceContainer(phase = startupPhase, modifier = Modifier.weight(1f)) {
                        when (screen) {
                            Screen.Home -> {
                                 HomeScreen(
                                     state = visualState,
                                    personalizationPreviewState = personalizationPreviewState,
                                    mode = homeMode,
                                    adaptiveMetrics = homeAdaptiveMetrics,
                                    weekCardHeight = weekCardHeight.dp,
                                    displayWeek = homeDisplayWeek,
                                    displayDate = homeDisplayDate,
                                    backdrop = backgroundBackdrop,
                                    // Dragged week cards must not sample contentBackdrop/chromeBackdrop:
                                    // they live inside contentBackdrop, so sampling it can recursively include themselves.
                                    floatingCourseBackdrop = backgroundBackdrop,
                                     weekHeaderBackdrop = backgroundBackdrop,
                                    onSwipeWeek = { delta -> homeDisplayWeek = (homeDisplayWeek + delta).coerceIn(1, visualState.config.totalWeeks.coerceAtLeast(1)) },
                                    onSwipeDay = { delta ->
                                        val requested = homeDisplayDate.plusDays(delta.toLong())
                                        val range = scheduleDayNavigationRange(visualState.config, todayDate)
                                        homeDisplayDate = if (range == null) requested
                                        else requested.coerceIn(range.start, range.endInclusive)
                                    },
                                    onContentUnderTopBarChange = { homeContentUnderTopBar = it },
                                    onCourseClick = { course, week, sourceBounds ->
                                        openCourseEditor(course, week, sourceBounds)
                                    },
                                    onAddCourse = viewModel::addCourse,
                                    onUpdateCourseSingleWeek = viewModel::updateCourseSingleWeek,
                                    conflictFocusCourseId = pendingConflictCourseId
                                        ?.takeIf { homeDisplayWeek in pendingConflictWeeks },
                                    conflictFocusCourseKey = pendingConflictCourseKey
                                        ?.takeIf { homeDisplayWeek in pendingConflictWeeks },
                                    onResolveCourseConflict = { course, moved, week ->
                                            viewModel.updateCourseSingleWeek(course, moved, week)
                                        appScope.launch {
                                            delay(180)
                                            pendingConflictCourseId = null
                                            pendingConflictCourseKey = null
                                            pendingConflictWeeks = emptyList()
                                        }
                                    },
                                     onDeleteCourseSingleWeek = viewModel::deleteCourseSingleWeek,
                                      weekEditInteractionEnabled = pickerState.phase is CustomizeUiState.Home,
                                      courseGlassOcclusionPhase = effectiveCourseGlassOcclusionPhase,
                                      courseGlassRestoredGroupKeys = courseGlassRestoredGroupKeys,
                                      onScheduleLongPress = {
                                        if (pickerState.phase is CustomizeUiState.Home) {
                                            pendingHomeAnchoredOverlay = null
                                            homeAnchoredOverlayRequest = null
                                            pickerState.phase = CustomizeUiState.ShowingEntryButton
                                            showScheduleEntryPill = true
                                            prewarmCurrentScheduleSnapshot()
                                        }
                                    }
                                )
                            }
                            Screen.Config -> Box(Modifier.fillMaxSize()) {
                                SettingsScreen(
                                        page = SettingsPage.Root,
                                        state = state,
                                        backdrop = backgroundBackdrop,
                                        onPageChange = {
                                            val intent = Intent(context, SettingsDetailActivity::class.java)
                                                .putExtra(SettingsDetailPageExtra, it.name)
                                            if (it == SettingsPage.Schedule) {
                                                intent.putExtra(ScheduleCustomizeIdExtra, state.config.id)
                                            }
                                            context.openRegisteredActivity(
                                                TransitionRouteId.HomeToSettingsDetail,
                                                intent
                                            )
                                        },
                                        onSave = viewModel::saveConfig,
                                        onUpdateConfig = viewModel::saveNotificationSettings,
                                        onUpdateGeneralConfig = viewModel::saveGeneralSettings,
                                        onUpdateHomeChromeBlurScale = viewModel::saveHomeChromeBlurScale,
                                        exitCommitRequest = settingsExitRequest,
                                        onExitCommitFinished = { completed ->
                                            val action = pendingSettingsExitAction
                                            pendingSettingsExitAction = null
                                            if (completed) action?.invoke()
                                        },
                                        onExitInterceptionChange = {
                                            settingsExitInterceptionRequired = it
                                        }
                                    )
                            }
                        }
                        if (screen is Screen.Home) {
                            DockBackdropContinuityPatch(
                                config = visualState.config,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            if (screen !is Screen.Home) {
                showScheduleEntryPill = false
                if (pickerState.phase is CustomizeUiState.ShowingEntryButton) pickerState.phase = CustomizeUiState.Home
            }
            if (screen is Screen.Home || screen is Screen.Config) {
                DockEntranceContainer(
                    phase = startupPhase,
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    FloatingDock(
                        selected = screen,
                        backdrop = chromeBackdrop,
                        config = if (screen is Screen.Home) visualState.config else state.config,
                        homeMode = homeMode,
                        onHomeModeChange = { homeMode = it },
                        onHome = { requestSettingsExit { screen = Screen.Home } },
                        onConfig = {
                            screen = Screen.Config
                        }
                    )
                }
            }
        }
        }

        // This control intentionally lives outside the recorded home layer. It can be visible
        // while the real home is pre-captured without becoming part of its own preview bitmap.
        if (screen is Screen.Home) {
            ScheduleManagerEntryPill(
                visible = showScheduleEntryPill,
                backdrop = chromeBackdrop,
                config = visualState.config,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .zIndex(89f),
                onClick = ::enterCustomizePage,
                onDismiss = {
                    entryPrewarmJob?.cancel()
                    showScheduleEntryPill = false
                    if (pickerState.phase is CustomizeUiState.ShowingEntryButton) {
                        pickerState.phase = CustomizeUiState.Home
                    }
                }
            )
        }

        pickerState.snapshotCoverBitmap?.let { cover ->
            Image(
                bitmap = cover.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize().zIndex(90f)
            )
        }

        SchedulePickerOverlay(
            pickerState = pickerState,
            allState = allSchedulesState,
            backdrop = chromeBackdrop,
            dialogBackdrop = pickerSceneBackdrop,
            onPageSelected = ::switchPickerSchedule,
            onApply = { exitPicker(apply = true) },
            onClose = { exitPicker(apply = false) },
            onBack = { centeredId ->
                exitPicker(apply = false)
            },
            onCreate = {
                if (pickerState.phase is CustomizeUiState.Picker) {
                    snapshotJob?.cancel()
                    pickerState.phase = CustomizeUiState.CreatingCombination
                    viewModel.createSchedule(activate = false) { newId ->
                        appScope.launch {
                            snapshotFlow {
                                val latest = latestAllSchedulesState.value
                                latest.schedules.any { it.id == newId } &&
                                    latest.allConfigs.any { it.id == newId } &&
                                    latest.allPeriods.any { it.scheduleId == newId }
                            }.first { it }
                            pickerState.temporaryIds += newId
                            // Keep the currently centered card as the transition source. The new
                            // schedule is rendered underneath and revealed by the same crossfade
                            // hand-off used by Cancel, so no fake card or Pager insertion is needed.
                            pickerState.phase = CustomizeUiState.Picker
                            exitPicker(
                                apply = true,
                                targetOverride = newId,
                                commitTarget = true,
                                crossfadeToTarget = true,
                                onFinished = {
                                    quickScheduleDraft = quickDraftFor(newId)
                                }
                            )
                        }
                    }
                }
            },
            onShare = { scheduleId, shareType ->
                val profile = allSchedulesState.schedules.firstOrNull { it.id == scheduleId }
                val config = allSchedulesState.allConfigs.firstOrNull { it.id == scheduleId } ?: defaultConfig(scheduleId)
                val periods = allSchedulesState.allPeriods.filter { it.scheduleId == scheduleId }.ifEmpty { defaultPeriods(scheduleId) }
                val courses = allSchedulesState.allCourses.filter { it.scheduleId == scheduleId }
                val scheduleName = profile?.name ?: "课表"
                when (shareType) {
                    ScheduleShareType.TOKEN -> shareScheduleToken(
                        context,
                        scheduleName,
                        buildSleepDownScheduleToken(config, periods, courses)
                    )
                    ScheduleShareType.ICS -> appScope.launch {
                        runCatching {
                            withContext(Dispatchers.IO) {
                                IcsScheduleCodec.writeShareFile(context, scheduleName, config, periods, courses)
                            }
                        }.onSuccess { shareScheduleIcs(context, scheduleName, it) }
                            .onFailure { Toast.makeText(context, it.message ?: "ICS 文件生成失败", Toast.LENGTH_SHORT).show() }
                    }
                }
            },
            onCustomize = { scheduleId ->
                if (pickerState.phase is CustomizeUiState.Picker) {
                    quickScheduleDraft = quickDraftFor(scheduleId)
                }
            },
            onRename = viewModel::renameSchedule,
            onDeleteRequest = { scheduleId ->
                if (pickerState.phase is CustomizeUiState.Picker && pickerState.orderIds.size > 1) {
                    snapshotJob?.cancel()
                    pickerState.phase = CustomizeUiState.DeletingCombination
                    appScope.launch {
                        val animation = Animatable(0f)
                        animation.animateTo(1f, tween(280)) {
                            pickerState.deleteScale = 1f - value * 0.4f
                            pickerState.deleteAlpha = 1f - value
                        }
                        val deletedIndex = pickerState.orderIds.indexOf(scheduleId)
                        pickerState.orderIds.remove(scheduleId)
                        pickerState.temporaryIds.remove(scheduleId)
                        pickerState.snapshots.remove(scheduleId)
                        appScope.launch { ScheduleSnapshotStore.delete(context, scheduleId) }
                        viewModel.deleteSchedule(scheduleId)
                        val nextId = pickerState.orderIds.getOrNull(deletedIndex.coerceAtMost(pickerState.orderIds.lastIndex))
                            ?: pickerState.orderIds.firstOrNull()
                        pickerState.deletingScheduleId = null
                        pickerState.deleteReveal = 0f
                        pickerState.deleteScale = 1f
                        pickerState.deleteAlpha = 1f
                        pickerState.phase = CustomizeUiState.Picker
                        nextId?.let(::switchPickerSchedule)
                    }
                }
            },
            // Capture the complete manager (cards, title, indicators and actions) in a producer
            // that does not contain the later quick-settings sheet consumer.
            modifier = Modifier.glassBackdropProducer(pickerSceneBackdrop)
        )
    }
    } // end cached home content
    } // end HomeBackgroundBlurLayer
    } // end HomeBackgroundZoomLayer

    val latestOpenHomeMenuDestination = rememberUpdatedState<(HomeMenuDestinationKind) -> Unit> {
        kind -> openHomeMenuDestination(kind)
    }
    fun releaseHomeMenuActivitySource() {
        homeMenuActivityLaunched = false
        destinationOwnsButtonReturn = false
        destinationCollapseHandedOff = false
        homeMenuSourceHidden = false
    }
    val latestOpenHomeActivityDestination =
        rememberUpdatedState<(TransitionRouteId, Intent) -> Unit> { routeId, targetIntent ->
            if (homeMenuActivityLaunched) return@rememberUpdatedState
            val activity = context.findActivity() ?: return@rememberUpdatedState
            val sharedDestinationRequest =
                currentHomeMenuDestinationRequest(HomeMenuDestinationKind.AddCourse)
                    ?: return@rememberUpdatedState
            val sourceBoundsInRoot = sharedDestinationRequest.sourceBoundsInRoot
            val collapseBoundsInRoot = sharedDestinationRequest.collapseBoundsInRoot
            if (sourceBoundsInRoot.width <= 2f || sourceBoundsInRoot.height <= 2f) {
                return@rememberUpdatedState
            }
            homeMenuActivityLaunched = true
            appScope.launch {
            var openingSourceHandoffOwnedBySession = false
            var openingSourcePlaceholder: BitmapDrawable? = null
            val openingSourceOverlay = activity.window.decorView.overlay
            try {
                val fullFrame = runCatching {
                    detailScreenGraphicsLayer.toImageBitmap().asAndroidBitmap()
                }.getOrNull()
                val sourceSnapshot = fullFrame?.cropToAnchoredBounds(sourceBoundsInRoot)
                val collapseSnapshot = fullFrame?.cropToAnchoredBounds(collapseBoundsInRoot)
                // Never hide the accepted glass menu unless both the complete opening source and
                // the real top-right return button have been captured successfully.
                if (sourceSnapshot == null || collapseSnapshot == null) {
                    releaseHomeMenuActivitySource()
                    return@launch
                }
                val sourceBoundsInWindow = Rect(
                    left = sourceBoundsInRoot.left + homeRootPositionInWindow.x,
                    top = sourceBoundsInRoot.top + homeRootPositionInWindow.y,
                    right = sourceBoundsInRoot.right + homeRootPositionInWindow.x,
                    bottom = sourceBoundsInRoot.bottom + homeRootPositionInWindow.y
                )
                val collapseBoundsInWindow = Rect(
                    left = collapseBoundsInRoot.left + homeRootPositionInWindow.x,
                    top = collapseBoundsInRoot.top + homeRootPositionInWindow.y,
                    right = collapseBoundsInRoot.right + homeRootPositionInWindow.x,
                    bottom = collapseBoundsInRoot.bottom + homeRootPositionInWindow.y
                )
                openingSourcePlaceholder = BitmapDrawable(context.resources, sourceSnapshot).apply {
                    setBounds(
                        sourceBoundsInWindow.left.roundToInt(),
                        sourceBoundsInWindow.top.roundToInt(),
                        sourceBoundsInWindow.right.roundToInt(),
                        sourceBoundsInWindow.bottom.roundToInt()
                    )
                }
                val sourceHandoffAttached = runCatching {
                    openingSourceOverlay.add(checkNotNull(openingSourcePlaceholder))
                    true
                }.getOrDefault(false)
                if (!sourceHandoffAttached) {
                    releaseHomeMenuActivitySource()
                    return@launch
                }
                // Preserve the exact clicked menu until either ColorOS actually starts its spring
                // or the Legacy backend synchronously installs its own placeholder. The hidden
                // Compose frame below is still what the destination backdrop records.
                homeMenuSourceHidden = true
                withFrameNanos { }
                withFrameNanos { }
                val cleanBackground = runCatching {
                    detailScreenGraphicsLayer.toImageBitmap().asAndroidBitmap()
                }.getOrNull()
                if (cleanBackground == null) {
                    releaseHomeMenuActivitySource()
                    return@launch
                }
                val openingAnchor = TransitionAnchorFrame(
                    boundsInWindow = sourceBoundsInWindow,
                    cornerRadiusPx = with(density) { HomeAddMenuTargetCornerDp.dp.toPx() },
                    bitmap = sourceSnapshot
                )
                val returnAnchor = TransitionAnchorFrame(
                    boundsInWindow = collapseBoundsInWindow,
                    cornerRadiusPx = with(density) { 21.dp.toPx() },
                    bitmap = collapseSnapshot
                )
                val launchResult = ActivityTransitionCoordinator.open(
                    activity = activity,
                    routeId = routeId,
                    intent = targetIntent,
                    payload = TransitionPayload(
                        openingAnchor = openingAnchor,
                        returnAnchorProvider = StaticTransitionAnchorProvider(returnAnchor),
                        backgroundBitmap = cleanBackground,
                        onOpeningSourceHandoff = {
                            activity.runOnUiThread {
                                openingSourcePlaceholder?.let(openingSourceOverlay::remove)
                                openingSourcePlaceholder = null
                            }
                        },
                        // The source placeholder intentionally remains visible across the async
                        // Activity launch. Ask the public ColorOS API to alpha the source Activity
                        // leash when its native animation starts, preventing a stationary menu
                        // from remaining below the moving system bitmap.
                        nativeSourceLeashAlphaOutOnOpen = true,
                        // Source onResume happens before ColorOS finishes its CLOSE spring. Let
                        // the session-scoped backend release the hidden menu/button only after the
                        // matching native end callback (or Legacy completion/watchdog).
                        onSourceReleased = {
                            activity.runOnUiThread { releaseHomeMenuActivitySource() }
                        }
                    )
                )
                if (launchResult is TransitionLaunchResult.Failed) {
                    releaseHomeMenuActivitySource()
                    return@launch
                }
                openingSourceHandoffOwnedBySession = true
                // The Activity now owns the exact destination return to the real three-dot button.
                // Silently dispose the first-level menu underneath it instead of restoring that
                // menu as a false return anchor.
                destinationOwnsButtonReturn = true
                destinationCollapseHandedOff = false
                homeAnchoredOverlayRequest = null
            } catch (_: Throwable) {
                releaseHomeMenuActivitySource()
            } finally {
                if (!openingSourceHandoffOwnedBySession) {
                    openingSourcePlaceholder?.let(openingSourceOverlay::remove)
                    openingSourcePlaceholder = null
                }
            }
        }
        }
    val latestOpenCourseManagement = rememberUpdatedState<() -> Unit> {
        latestOpenHomeActivityDestination.value(
            TransitionRouteId.HomeToCourseManagement,
            Intent(context, CourseManagementActivity::class.java)
                .putCourseManagementInitialState(state)
        )
    }
    val latestOpenJumpWeekDialog = rememberUpdatedState<() -> Unit> {
        pendingJumpWeekDialog = true
        homeAnchoredOverlayRequest = null
    }
    val latestOpenScheduleSettings = rememberUpdatedState<() -> Unit> {
        pendingOpenScheduleSettings = true
        homeAnchoredOverlayRequest = null
    }
    val homeAddActions = remember {
        listOf(
            AddMenuAction(R.drawable.ic_add_course, "添加单节课") {
                latestOpenHomeMenuDestination.value(HomeMenuDestinationKind.AddCourse)
            },
            AddMenuAction(R.drawable.ic_ai_import, "手动导入课表") {
                latestOpenHomeMenuDestination.value(HomeMenuDestinationKind.ManualImport)
            },
            AddMenuAction(R.drawable.ic_school_import, "教务系统导入") {
                homeAnchoredOverlayRequest = null
                Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show()
            },
            AddMenuAction(R.drawable.ic_courses, "课程管理") {
                latestOpenCourseManagement.value()
            },
            AddMenuAction(R.drawable.ic_material_event, "跳转周数") {
                latestOpenJumpWeekDialog.value()
            },
            AddMenuAction(R.drawable.ic_settings, "课表设置") {
                latestOpenScheduleSettings.value()
            }
        )
    }

    LaunchedEffect(
        pendingOpenScheduleSettings,
        homeAnchoredMorphState.phase,
        screen,
        state.config.id
    ) {
        if (!pendingOpenScheduleSettings || screen !is Screen.Home) return@LaunchedEffect
        if (homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle) return@LaunchedEffect
        pendingOpenScheduleSettings = false
        if (pickerState.phase is CustomizeUiState.Home) {
            // Equivalent to long-pressing the schedule homepage and tapping the 课表设置
            // entry pill: open the in-app multi-schedule customization page directly.
            pickerState.phase = CustomizeUiState.ShowingEntryButton
            showScheduleEntryPill = true
            prewarmCurrentScheduleSnapshot()
            enterCustomizePage()
        }
    }

    LaunchedEffect(
        pendingJumpWeekDialog,
        homeAnchoredMorphState.phase,
        screen,
        state.config.id
    ) {
        if (!pendingJumpWeekDialog || screen !is Screen.Home) return@LaunchedEffect
        if (homeAnchoredMorphState.phase != HomeAnchoredOverlayPhase.Idle) return@LaunchedEffect
        pendingJumpWeekDialog = false
        // Open the jump-week dialog only after the three-dot menu has fully retracted, so the
        // dialog never overlaps the closing menu.
        jumpWeekDialogMounted = true
        jumpWeekDialogVisible = true
    }

    LaunchedEffect(sourceButtonFollowThrough) {
        val follow = sourceButtonFollowThrough ?: return@LaunchedEffect
        sourceButtonFollowProgress.snapTo(0f)
        sourceButtonFollowProgress.animateTo(
            1f,
            spring(dampingRatio = 0.86f, stiffness = 460f)
        )
        sourceButtonFollowThrough = null
    }

    fun closeHomeMenuDestination() {
        // The destination owns the legacy liquid return to the real button. Dispose the hidden
        // first-level menu without replaying its independent Closing or button follow-through.
        destinationOwnsButtonReturn = true
        destinationCollapseHandedOff = false
        homeAnchoredOverlayRequest = null
        homeMenuDestinationRequest = null
    }

    HomeAnchoredMorphOverlayHost(
        request = if (screen is Screen.Home) homeAnchoredOverlayRequest else null,
        motionState = homeAnchoredMorphState,
        backdrop = homeAnchoredOverlayBackdrop,
        config = state.config,
        addActions = homeAddActions,
        adaptiveMetrics = homeAdaptiveMetrics,
        modifier = Modifier
            .zIndex(24f)
            .graphicsLayer { alpha = if (homeMenuSourceHidden) 0f else 1f },
        awaitOpeningGate = {
            awaitCourseGlassOpeningGate(substantialHomeAnchoredCoverage)
        },
        onDismissRequest = {
            homeAnchoredOverlayRequest = null
        },
        onAddMenuBoundsChanged = { homeAddMenuBoundsInRoot = it },
        onSourceFollowThrough = { rect -> latestSourceFollowThrough.value(rect) },
        suppressClose = destinationOwnsButtonReturn,
        personalizePreviewProgress = personalizationPreviewProgress,
        sourceContent = { kind, sourceModifier ->
            HomeIconButtonVisual(
                backdrop = homeAnchoredOverlayBackdrop,
                config = state.config,
                iconRes = if (kind == HomeAnchoredOverlayKind.Personalize) {
                    R.drawable.ic_edit
                } else {
                    R.drawable.ic_more_horizontal
                },
                contentDescription = if (kind == HomeAnchoredOverlayKind.Personalize) {
                    "benchmark_personalize_button"
                } else {
                    "添加菜单"
                },
                modifier = sourceModifier,
                isInteractive = false
            )
        },
        personalizeContent = { panelModifier ->
            PersonalizePanel(
                modifier = panelModifier.semantics { testTag = "benchmark_personalize_panel" },
                drawSurface = false,
                state = visualState,
                backdrop = homeAnchoredOverlayBackdrop,
                mode = homeMode,
                weekCardHeightScale = weekCardHeightScale,
                weekCardHeightScaleFloor = weekCardHeightScaleFloor,
                onWeekCardHeightScale = {
                    val safeScale = normalizedWeekCardHeightScale(it, weekCardHeightScaleFloor)
                    val committedConfig = mergePersonalizationCandidate(
                        current = personalizationDraftConfig ?: visualState.config,
                        candidate = visualState.config.copy(
                            weekCardHeightDp = null,
                            weekCardHeightScale = safeScale
                        ),
                        changeKey = PersonalizeWeekHeightSlider
                    )
                    weekCardHeightScale = safeScale
                    personalizationDraftConfig = committedConfig
                    personalizationPendingCommitConfig = committedConfig
                    viewModel.savePersonalization(committedConfig)
                },
                onWeekCardHeightScalePreview = {
                    weekCardHeightScale = normalizedWeekCardHeightScale(it, weekCardHeightScaleFloor)
                },
                onPickWallpaper = {
                    wallpaperLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onUpdateConfig = { changeKey, candidate ->
                    val merged = mergePersonalizationCandidate(
                        current = personalizationDraftConfig ?: visualState.config,
                        candidate = candidate,
                        changeKey = changeKey
                    )
                    personalizationDraftConfig = merged
                    personalizationPendingCommitConfig = merged
                    if (changeKey != PersonalizeWallpaperBlurSlider) {
                        personalizationPreviewState.clear(changeKey)
                    }
                    viewModel.savePersonalization(merged)
                },
                onPreviewConfig = { sliderKey, candidate ->
                    personalizationPreviewState.update(sliderKey, candidate)
                },
                previewSliderKey = personalizationSliderPreviewKey,
                previewProgress = personalizationPreviewProgress,
                onSliderPreviewActiveChange = { key, active ->
                    personalizationSliderPreviewKey = resolveActivePersonalizationSlider(
                        currentKey = personalizationSliderPreviewKey,
                        eventKey = key,
                        active = active
                    )
                }
            )
        }
    )

    // The first-level menu alone owns this final button follow-through. Destinations restore the
    // real button directly at their legacy pinch handoff and never enter this spring choreography.
    sourceButtonFollowThrough?.let { follow ->
        val followProgress = sourceButtonFollowProgress.value
        val rest = addButtonBounds ?: return@let
        val density = LocalDensity.current
        val buttonSizePx = with(density) { 42.dp.toPx() }
        val progress = followProgress
        val center = Offset(
            x = follow.startCenter.x + (rest.center.x - follow.startCenter.x) * progress,
            y = follow.startCenter.y + (rest.center.y - follow.startCenter.y) * progress
        )
        val scale = follow.startScale + (1f - follow.startScale) * progress
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (center.x - buttonSizePx / 2f).roundToInt(),
                        (center.y - buttonSizePx / 2f).roundToInt()
                    )
                }
                .requiredSize(42.dp)
                .zIndex(300f)
        ) {
            HomeIconButtonVisual(
                backdrop = homeAnchoredOverlayBackdrop,
                config = state.config,
                iconRes = R.drawable.ic_more_horizontal,
                contentDescription = "添加菜单",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
    }

    if (jumpWeekDialogMounted) {
        HomeJumpWeekDialog(
            show = jumpWeekDialogVisible,
            initialWeek = homeDisplayWeek,
            totalWeeks = visualState.config.totalWeeks,
            backdrop = homeDialogBackdrop,
            config = visualState.config,
            onDismissRequest = { jumpWeekDialogVisible = false },
            onDismissFinished = { targetWeek ->
                jumpWeekDialogVisible = false
                jumpWeekDialogMounted = false
                targetWeek?.let { requestedWeek ->
                    val boundedWeek = requestedWeek.coerceIn(
                        1,
                        visualState.config.totalWeeks.coerceAtLeast(1)
                    )
                    appScope.launch {
                        if (homeMode != HomeMode.Week) {
                            homeMode = HomeMode.Week
                            // Let the current week page become the settled week surface first;
                            // the selected target then enters through the normal week transition.
                            delay(220)
                        } else {
                            withFrameNanos { }
                        }
                        homeDisplayWeek = boundedWeek
                    }
                }
            }
        )
    }

    HomeMenuDestinationOverlayHost(
        request = homeMenuDestinationRequest,
        motionState = homeMenuDestinationMotionState,
        state = state,
        backdrop = homeMenuDestinationBackdrop,
        adaptiveMetrics = homeAdaptiveMetrics,
        modifier = Modifier.zIndex(90f),
        awaitOpeningGate = { awaitCourseGlassOpeningGate(routeEligible = true) },
        onDismissRequest = ::closeHomeMenuDestination,
        sourceActions = homeAddActions,
        onSourceHandoff = { homeMenuSourceHidden = true },
        onCollapseHandoff = {
            destinationCollapseHandedOff = true
        },
        onClosed = {
            homeMenuSourceHidden = false
            destinationCollapseHandedOff = false
            destinationOwnsButtonReturn = false
        },
        onAddCourses = { courses ->
            val conflictWeeks = conflictWeeksForAddedCourses(courses, state.courses, state.periods)
            if (conflictWeeks.isEmpty()) {
                viewModel.addCourses(courses)
                closeHomeMenuDestination()
            } else {
                pendingCourseGroupEdit = PendingCourseGroupEdit(emptyList(), courses)
                homeDialog = HomeDialog.ConfirmCourseConflicts(
                    original = courses.first(),
                    edited = courses.first(),
                    targetWeek = effectiveCurrentWeek(state.config),
                    conflictWeeks = conflictWeeks
                )
            }
        },
        onManualImportParsed = { draft ->
            closeHomeMenuDestination()
            appScope.launch {
                delay(HomeMenuDestinationCloseDurationMillis.toLong())
                homeDialog = HomeDialog.ConfirmImport(draft, returnDialog = null)
            }
        },
    )
    GlassMiuixSettingsTheme(settingsVisualConfig(state.config)) {
        QuickScheduleSettingsSheets(
            draft = quickScheduleDraft,
            config = state.config,
            // The home/chrome producers are siblings below the sheet, so this remains a real
            // liquid backdrop without ever recording the dialog that consumes it. In particular,
            // do not restore the former root-level quickSheetBackdrop: it caused the native
            // RenderThread recursion when the new-schedule sheet opened after Picker exit.
            backdrop = if (pickerState.overlayVisible) pickerSceneBackdrop else chromeBackdrop,
            onDraftChange = { quickScheduleDraft = it },
            onDismiss = { quickScheduleDraft = null },
            onDismissFinished = {
                // Direct customization leaves the manager below the sheet. New-schedule setup
                // still returns through the existing home-to-picker Morph after its sheet closes.
                if (!pickerState.overlayVisible) {
                    pickerState.phase = CustomizeUiState.Home
                    enterCustomizePage()
                }
            },
            onSave = { draft, onSaved ->
                val latest = latestAllSchedulesState.value
                val baseConfig = latest.allConfigs.firstOrNull { it.id == draft.scheduleId }
                    ?: return@QuickScheduleSettingsSheets
                val totalWeeks = draft.totalWeeks.coerceIn(1, 60)
                val manualWeek = draft.currentWeek.coerceIn(1, totalWeeks)
                val datedConfig = baseConfig.copy(
                    totalWeeks = totalWeeks,
                    currentWeek = manualWeek,
                    termStartDate = draft.termStartDate.ifBlank { null },
                    autoCurrentWeek = draft.autoCurrentWeek,
                    hideEmptyWeekends = draft.hideEmptyWeekends
                )
                val periods = latest.allPeriods.filter { it.scheduleId == draft.scheduleId }
                    .ifEmpty { defaultPeriods(draft.scheduleId) }
                viewModel.saveConfigForSchedule(
                    draft.scheduleId,
                    // The automatic display week is derived at runtime. Persist the
                    // selected fallback instead of writing a clamped pre-term week 1.
                    datedConfig.copy(currentWeek = manualWeek),
                    periods,
                    onSaved
                )
            },
            suppressDetailedButton = detailMorphState !is DetailMorphState.Idle,
            onDetailedSettings = { scheduleId, sourceBoundsInWindow, saveBeforeOpening ->
                // The detailed page is the real cross-activity SettingsDetailActivity opened
                // through the shared transition framework (QuickSheetToSettingsDetail route).
                if (detailMorphState !is DetailMorphState.Idle) {
                    return@QuickScheduleSettingsSheets
                }
                val activity = context.findActivity() ?: return@QuickScheduleSettingsSheets
                appScope.launch {
                    suspend fun capturePopupFrame(): Bitmap? {
                        detailPopupCaptureActive = true
                        val requestedToken = detailPopupCaptureToken + 1
                        detailPopupCaptureToken = requestedToken
                        var waitedFrames = 0
                        while (
                            detailPopupCapturedToken.get() != requestedToken &&
                            waitedFrames < 4
                        ) {
                            withFrameNanos { }
                            waitedFrames += 1
                        }
                        if (detailPopupCapturedToken.get() != requestedToken) return null
                        return runCatching {
                            detailPopupGraphicsLayer.toImageBitmap().asAndroidBitmap()
                        }.getOrNull()
                    }

                    // Let the released press state reach the source window before copying its
                    // final compositor output. This is the authoritative path for the custom
                    // schedule picker, whose nested RenderNodes cannot be flattened reliably by
                    // recording only the outer Compose layer.
                    withFrameNanos { }
                    val windowSnapshot = captureDetailMorphWindowSnapshot(
                        activity = activity,
                        rootPositionInWindow = homeRootPositionInWindow,
                        rootSize = homeReadabilityRootSize
                    )
                    val layeredSnapshot = if (windowSnapshot == null) {
                        val sourceUnderlaySnapshot = runCatching {
                            detailScreenGraphicsLayer.toImageBitmap().asAndroidBitmap()
                        }.getOrNull()
                        val sourcePopupSnapshot = try {
                            capturePopupFrame()
                        } finally {
                            detailPopupCaptureActive = false
                        }
                        if (sourceUnderlaySnapshot != null && sourcePopupSnapshot != null) {
                            composeDetailMorphSnapshot(sourceUnderlaySnapshot, sourcePopupSnapshot)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                    val fullSnapshot = windowSnapshot?.bitmap ?: layeredSnapshot
                    // This source is not a normal page: the home underlay and QuickSheet are two
                    // sibling draw layers in the root Miuix Scaffold. Preserve their already
                    // composed frame for the destination-side depth/blur renderer; cross-window
                    // blur cannot reconstruct that Compose backdrop topology on ColorOS.
                    val snapshotOriginInWindow = windowSnapshot?.originInWindow
                        ?: homeRootPositionInWindow
                    val sourceBoundsInSnapshot = Rect(
                        left = sourceBoundsInWindow.left - snapshotOriginInWindow.x,
                        top = sourceBoundsInWindow.top - snapshotOriginInWindow.y,
                        right = sourceBoundsInWindow.right - snapshotOriginInWindow.x,
                        bottom = sourceBoundsInWindow.bottom - snapshotOriginInWindow.y
                    )
                    val buttonSnapshot = fullSnapshot?.cropToAnchoredBounds(sourceBoundsInSnapshot)
                    val openingAnchor = TransitionAnchorFrame(
                        boundsInWindow = sourceBoundsInWindow,
                        cornerRadiusPx = with(density) { 25.dp.toPx() },
                        bitmap = buttonSnapshot
                    )
                    // The destination must not race the draft write. Keeping the real button in
                    // place while saving gives the translucent destination an unchanged live
                    // underlay and ensures the first detailed-settings composition sees the
                    // committed values.
                    saveBeforeOpening {
                        appScope.launch {
                            ActivityTransitionCoordinator.open(
                                activity = activity,
                                routeId = TransitionRouteId.QuickSheetToSettingsDetail,
                                intent = Intent(activity, QuickSheetSettingsDetailActivity::class.java)
                                    .putExtra(SettingsDetailPageExtra, SettingsPage.Schedule.name)
                                    .putExtra(ScheduleCustomizeIdExtra, scheduleId),
                                payload = TransitionPayload(
                                    openingAnchor = openingAnchor,
                                    returnAnchorProvider = StaticTransitionAnchorProvider(openingAnchor),
                                    backgroundBitmap = fullSnapshot
                                )
                            )
                        }
                    }
                }
            }
        )

        // Keep the course editor inside the same root stack as the stock MIUIX popup
        // host. The host stays in its 1.0.6 position and therefore preserves the manager
        // quick sheet's original backdrop, colors and animation, while its later draw
        // order still places picker dialogs above the editor shell.
        CourseEditorContainerOverlayHost(
            request = courseEditorRequest,
            state = state,
            backdrop = courseEditorBackdrop,
            config = state.config,
            adaptiveMetrics = homeAdaptiveMetrics,
            modifier = Modifier.zIndex(100f),
            awaitOpeningGate = { awaitCourseGlassOpeningGate(routeEligible = true) },
            onDismissRequest = { closeCourseEditor() },
            onSave = { originals, editedCourses, targetWeek ->
                val original = originals.singleOrNull()
                val edited = editedCourses.singleOrNull()
                if (original != null && edited != null && courseWeeksChanged(original, edited)) {
                    val conflictWeeks = conflictWeeksForEditedCourse(
                        original,
                        edited,
                        state.courses,
                        state.periods
                    )
                    if (conflictWeeks.isEmpty()) {
                        viewModel.updateCourse(edited)
                        closeCourseEditor()
                    } else {
                        homeDialog = HomeDialog.ConfirmCourseConflicts(
                            original,
                            edited,
                            targetWeek ?: effectiveCurrentWeek(state.config),
                            conflictWeeks
                        )
                    }
                } else if (original != null && edited != null) {
                    // Keep the editor fully mounted behind the choice dialog. Closing it first
                    // lets its Morph/interaction shield cover and stall the confirmation.
                    homeDialog = HomeDialog.ApplyCourseEdit(original, edited, targetWeek ?: effectiveCurrentWeek(state.config))
                } else {
                    val conflictWeeks = conflictWeeksForEditedCourseGroup(
                        originals,
                        editedCourses,
                        state.courses,
                        state.periods
                    )
                    if (conflictWeeks.isEmpty()) {
                        viewModel.replaceCourseGroup(originals, editedCourses)
                        closeCourseEditor()
                    } else {
                        pendingCourseGroupEdit = PendingCourseGroupEdit(originals, editedCourses)
                        homeDialog = HomeDialog.ConfirmCourseConflicts(
                            originals.first(),
                            editedCourses.first(),
                            targetWeek ?: effectiveCurrentWeek(state.config),
                            conflictWeeks
                        )
                    }
                }
            },
            onDelete = { courses, targetWeek ->
                pendingCourseGroupDelete = courses.takeIf { it.size > 1 }.orEmpty()
                homeDialog = HomeDialog.ApplyCourseDelete(
                    courses.first(),
                    targetWeek ?: effectiveCurrentWeek(state.config)
                )
            },
            motionState = courseEditorMotionState,
            onRenderedCourseIdChange = { courseEditorRenderedCourseId = it },
            onPhaseChange = {}
        )

    }

    remoteExperience.agreement?.let { agreement ->
        LiquidAlertDialog(
            title = agreement.title,
            message = if (agreement.forceReaccept) {
                "协议或政策已经更新，请查看新版本并确认同意后继续使用。"
            } else {
                "首次使用前，请查看并同意此协议或政策。"
            },
            actions = listOf(
                LiquidAlertAction("查看正文", LiquidAlertActionStyle.Secondary) {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, agreement.url.toUri()))
                    }
                },
                LiquidAlertAction("同意", LiquidAlertActionStyle.Primary) {
                    SleepDownRemoteConfig.acceptAgreement(context, agreement)
                }
            ),
            backdrop = chromeBackdrop,
            config = state.config,
            onDismissRequest = {}
        )
    }

    (renderedHomeDialog as? HomeDialog.EditWallpaper)?.let { dialog ->
        WallpaperEditorOverlay(
            uri = dialog.uri,
            entrySnapshot = dialog.entrySnapshot,
            config = visualState.config,
            backdrop = chromeBackdrop,
            visible = homeDialogVisible,
            adaptiveMetrics = homeAdaptiveMetrics,
            onCancel = { dismissHomeDialog() },
            onApply = { nextConfig ->
                viewModel.savePersonalization(nextConfig)
                dismissHomeDialog()
            },
            modifier = Modifier.zIndex(280f)
        )
    }

    // Dialog-based dialogs for all other types (including EditCourse without a source card)
    renderedHomeDialog?.let { dialog ->
        if (dialog !is HomeDialog.EditWallpaper && (dialog !is HomeDialog.EditCourse || dialog.course == null)) {
        if (dialog is HomeDialog.ApplyCourseEdit) {
            ApplyCourseEditDialog(
                original = dialog.original,
                edited = dialog.edited,
                backdrop = homeDialogBackdrop,
                config = state.config,
                onSingle = {
                    val conflictWeeks = conflictWeeksForSingleWeekEdit(
                        dialog.original,
                        dialog.edited,
                        dialog.targetWeek,
                        state.courses,
                        state.periods
                    )
                    if (conflictWeeks.isEmpty()) {
                        viewModel.updateCourseSingleWeek(
                            dialog.original,
                            dialog.edited,
                            dialog.targetWeek
                        )
                        dismissHomeDialog()
                        closeCourseEditor()
                    } else {
                        homeDialog = HomeDialog.ConfirmCourseConflicts(
                            dialog.original,
                            dialog.edited,
                            dialog.targetWeek,
                            conflictWeeks,
                            singleWeekOnly = true
                        )
                    }
                },
                onAll = {
                    val conflictWeeks = conflictWeeksForEditedCourse(
                        dialog.original,
                        dialog.edited,
                        state.courses,
                        state.periods
                    )
                    if (conflictWeeks.isEmpty()) {
                        viewModel.updateCourse(dialog.edited)
                        dismissHomeDialog()
                        closeCourseEditor()
                    } else {
                        homeDialog = HomeDialog.ConfirmCourseConflicts(
                            dialog.original,
                            dialog.edited,
                            dialog.targetWeek,
                            conflictWeeks
                        )
                    }
                },
                onCancel = { dismissHomeDialog() }
            )
        } else if (dialog is HomeDialog.ConfirmCourseConflicts) {
            CourseConflictRetentionDialog(
                course = dialog.edited,
                conflictWeeks = dialog.conflictWeeks,
                backdrop = homeDialogBackdrop,
                config = state.config,
                onKeepTemporarily = keepConflict@{
                    pendingCourseGroupEdit?.let { groupEdit ->
                        if (groupEdit.originals.isEmpty()) {
                            viewModel.addCourses(groupEdit.edited)
                            pendingCourseGroupEdit = null
                            pendingConflictCourseId = null
                            pendingConflictCourseKey = groupEdit.edited.firstOrNull()?.occurrenceOverrideKey()
                            pendingConflictWeeks = dialog.conflictWeeks
                            dismissHomeDialog()
                            closeHomeMenuDestination()
                            val conflictWeek = dialog.conflictWeeks.first()
                            appScope.launch {
                                delay(HomeMenuDestinationCloseDurationMillis.toLong() + 32L)
                                homeDisplayWeek = conflictWeek
                                homeMode = HomeMode.Week
                            }
                            return@keepConflict
                        }
                        val editorWasOpen = courseEditorRequest != null
                        viewModel.replaceCourseGroup(groupEdit.originals, groupEdit.edited)
                        pendingCourseGroupEdit = null
                        pendingConflictCourseId = groupEdit.edited.firstOrNull()?.id?.takeIf { it > 0 }
                        pendingConflictCourseKey = groupEdit.edited.firstOrNull()?.occurrenceOverrideKey()
                        pendingConflictWeeks = dialog.conflictWeeks
                        dismissHomeDialog()
                        closeCourseEditor()
                        val conflictWeek = dialog.conflictWeeks.first()
                        if (editorWasOpen) {
                            appScope.launch {
                                delay(CourseEditorCloseDurationMillis.toLong() + 32L)
                                homeDisplayWeek = conflictWeek
                                homeMode = HomeMode.Week
                            }
                        } else {
                            homeDisplayWeek = conflictWeek
                            homeMode = HomeMode.Week
                        }
                        return@keepConflict
                    }
                    val editorWasOpen = courseEditorRequest != null
                    val editedRetractBounds = courseEditorRequest
                        ?.sourceBoundsInRoot
                        ?.takeIf { homeMode == HomeMode.Week }
                        ?.let { source ->
                            val periodIndexes = state.periods.map { it.periodIndex }
                            val originalStart = dialog.original.periods
                                .map(periodIndexes::indexOf)
                                .filter { it >= 0 }
                                .minOrNull()
                            val editedPositions = dialog.edited.periods
                                .map(periodIndexes::indexOf)
                                .filter { it >= 0 }
                                .distinct()
                                .sorted()
                            val editedStart = editedPositions.firstOrNull()
                            if (originalStart == null || editedStart == null) {
                                null
                            } else {
                                val cardHeightPx = with(density) { weekCardHeight.dp.toPx() }
                                val cardGapPx = with(density) { 4.dp.toPx() }
                                val left = source.left +
                                    (dialog.edited.weekday - dialog.original.weekday) *
                                    (source.width + cardGapPx)
                                val top = source.top +
                                    (editedStart - originalStart) * cardHeightPx
                                val targetHeight = (
                                    cardHeightPx * editedPositions.size.coerceAtLeast(1) -
                                        cardGapPx
                                    ).coerceAtLeast(with(density) { 18.dp.toPx() })
                                Rect(left, top, left + source.width, top + targetHeight)
                            }
                        }
                    courseEditorMotionState.retractTo(editedRetractBounds)
                    if (dialog.singleWeekOnly) {
                        viewModel.updateCourseSingleWeek(
                            dialog.original,
                            dialog.edited,
                            dialog.targetWeek
                        )
                    } else {
                        viewModel.updateCourse(dialog.edited)
                    }
                    pendingConflictCourseId =
                        dialog.edited.id.takeUnless { dialog.singleWeekOnly }
                    pendingConflictCourseKey = dialog.edited.occurrenceOverrideKey()
                    pendingConflictWeeks = dialog.conflictWeeks
                    dismissHomeDialog()
                    closeCourseEditor()
                    val conflictWeek = dialog.conflictWeeks.first()
                    if (editorWasOpen) {
                        appScope.launch {
                            delay(CourseEditorCloseDurationMillis.toLong() + 32L)
                            homeDisplayWeek = conflictWeek
                            homeMode = HomeMode.Week
                        }
                    } else {
                        homeDisplayWeek = conflictWeek
                        homeMode = HomeMode.Week
                    }
                },
                onReturn = {
                    if (pendingCourseGroupEdit != null) {
                        pendingCourseGroupEdit = null
                        dismissHomeDialog()
                    } else {
                        homeDialog = HomeDialog.ApplyCourseEdit(
                            dialog.original,
                            dialog.edited,
                            dialog.targetWeek
                        )
                    }
                }
            )
        } else if (dialog is HomeDialog.ApplyCourseDelete) {
            ApplyCourseDeleteDialog(
                course = dialog.course,
                backdrop = homeDialogBackdrop,
                config = state.config,
                onSingle = {
                    if (pendingCourseGroupDelete.isNotEmpty()) {
                        viewModel.deleteCoursesSingleWeek(pendingCourseGroupDelete, dialog.targetWeek)
                    } else {
                        viewModel.deleteCourseSingleWeek(dialog.course, dialog.targetWeek)
                    }
                    pendingCourseGroupDelete = emptyList()
                    dismissHomeDialog()
                    closeCourseEditor()
                },
                onAll = {
                    if (pendingCourseGroupDelete.isNotEmpty()) {
                        viewModel.deleteCourses(pendingCourseGroupDelete)
                    } else {
                        viewModel.deleteCourse(dialog.course)
                    }
                    pendingCourseGroupDelete = emptyList()
                    dismissHomeDialog()
                    closeCourseEditor()
                },
                onCancel = {
                    pendingCourseGroupDelete = emptyList()
                    dismissHomeDialog()
                }
            )
        } else {
        Dialog(onDismissRequest = { dismissHomeDialog() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            if (dialog is HomeDialog.ImportSchedule) {
                val dialogView = LocalView.current
                DisposableEffect(dialogView) {
                    val dialogWindow = (dialogView.parent as? DialogWindowProvider)?.window
                    val previousSoftInputMode = dialogWindow?.attributes?.softInputMode
                    dialogWindow?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                    onDispose {
                        previousSoftInputMode?.let { dialogWindow.setSoftInputMode(it) }
                    }
                }
            }
            val animatedDialogContent: @Composable () -> Unit = {
                AnimatedVisibility(
                    visible = homeDialogVisible,
                    enter = popEnterTransition(),
                    exit = popExitTransition()
                ) {
                val dialogContent: @Composable () -> Unit = {
                    when (dialog) {
                    is HomeDialog.EditCourse -> {
                        val editor: @Composable () -> Unit = {
                            NormalizedCourseEditorScreen(
                                state = state,
                                initialCourse = dialog.course,
                                onCancel = { dismissHomeDialog() },
                                onSave = {
                                    if (dialog.course == null) {
                                        viewModel.addCourse(it)
                                        dismissHomeDialog()
                                    } else if (courseWeeksChanged(dialog.course, it)) {
                                        viewModel.updateCourse(it)
                                        dismissHomeDialog()
                                    } else {
                                        homeDialog = HomeDialog.ApplyCourseEdit(dialog.course, it, dialog.targetWeek ?: effectiveCurrentWeek(state.config))
                                    }
                                },
                                onDelete = {
                                    homeDialog = HomeDialog.ApplyCourseDelete(it, dialog.targetWeek ?: effectiveCurrentWeek(state.config))
                                },
                                backdrop = homeDialogBackdrop,
                                pickerRenderInRootScaffold = dialog.course != null
                            )
                        }
                        if (dialog.course == null) {
                            // Keep the Android dialog itself under its original centered constraints.
                            // Only the editor content owns a local overlay host, so wheel pickers can
                            // cover the form without changing the glass shell's size or position.
                            top.yukonga.miuix.kmp.basic.Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = ComposeColor.Transparent,
                                contentWindowInsets = WindowInsets(0, 0, 0, 0)
                            ) {
                                editor()
                            }
                        } else {
                            editor()
                        }
                    }
                    HomeDialog.ImportSchedule -> NormalizedAiManualImportScreen(
                        state = state,
                        backdrop = homeDialogBackdrop,
                        onCancel = { dismissHomeDialog() },
                        onParsed = { homeDialog = HomeDialog.ConfirmImport(it, returnDialog = null) }
                    )
                    is HomeDialog.EditWallpaper -> Unit
                    is HomeDialog.ConfirmImport -> ConfirmScheduleScreen(
                        draft = dialog.draft,
                        backdrop = homeDialogBackdrop,
                        onCancel = { homeDialog = dialog.returnDialog },
                        onDraftChanged = { revised ->
                            homeDialog = dialog.copy(draft = revised)
                        },
                        onConfirm = { createNewSchedule ->
                            viewModel.importDraft(dialog.draft, createNewSchedule) { scheduleId ->
                                dismissHomeDialog()
                                pendingImportedSetupId = scheduleId
                            }
                        }
                    )
                            is HomeDialog.ApplyCourseEdit -> ApplyCourseEditDialog(
                        original = dialog.original,
                        edited = dialog.edited,
                        backdrop = homeDialogBackdrop,
                        config = state.config,
                        onSingle = {
                            val conflictWeeks = conflictWeeksForSingleWeekEdit(
                                dialog.original,
                                dialog.edited,
                                dialog.targetWeek,
                                state.courses,
                                state.periods
                            )
                            if (conflictWeeks.isEmpty()) {
                                viewModel.updateCourseSingleWeek(
                                    dialog.original,
                                    dialog.edited,
                                    dialog.targetWeek
                                )
                                dismissHomeDialog()
                            } else {
                                homeDialog = HomeDialog.ConfirmCourseConflicts(
                                    dialog.original,
                                    dialog.edited,
                                    dialog.targetWeek,
                                    conflictWeeks,
                                    singleWeekOnly = true
                                )
                            }
                        },
                            onAll = {
                            val conflictWeeks = conflictWeeksForEditedCourse(
                                dialog.original,
                                dialog.edited,
                                state.courses,
                                state.periods
                            )
                            if (conflictWeeks.isEmpty()) {
                                viewModel.updateCourse(dialog.edited)
                                dismissHomeDialog()
                            } else {
                                homeDialog = HomeDialog.ConfirmCourseConflicts(
                                    dialog.original,
                                    dialog.edited,
                                    dialog.targetWeek,
                                    conflictWeeks
                                )
                            }
                        },
                        onCancel = {
                            dismissHomeDialog()
                        }
                    )
                    is HomeDialog.ConfirmCourseConflicts -> Unit
                    is HomeDialog.ApplyCourseDelete -> ApplyCourseDeleteDialog(
                        course = dialog.course,
                        backdrop = homeDialogBackdrop,
                        config = state.config,
                        onSingle = {
                            viewModel.deleteCourseSingleWeek(dialog.course, dialog.targetWeek)
                            dismissHomeDialog()
                        },
                        onAll = {
                            viewModel.deleteCourse(dialog.course)
                            dismissHomeDialog()
                        },
                        onCancel = {
                            dismissHomeDialog()
                            openCourseEditor(dialog.course, dialog.targetWeek, null)
                        }
                    )
                    }
                }
                CenterLiquidDialog(
                        backdrop = homeDialogBackdrop,
                        config = state.config,
                        modifier = Modifier,
                        size = if (dialog is HomeDialog.ImportSchedule) {
                            LiquidDialogSize.Compact
                        } else {
                            LiquidDialogSize.Standard
                        },
                        followGlassContrast = dialog is HomeDialog.ImportSchedule
                    ) {
                        dialogContent()
                    }
                }
            }
            animatedDialogContent()
        }
        }
        }
        }

    }
    }
    }
    }

}

@Composable
private fun HomeBackgroundZoomLayer(
    zoom: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .graphicsLayer {
                val currentZoom = zoom()
                scaleX = currentZoom
                scaleY = currentZoom
            },
        content = content
    )
}

internal val HomeTopOverlayHeight = 178.dp
private val DetailTopBarHeight = SleepDownDesignTokens.SecondaryPage.CompactTopBarHeight
private val DetailTopOverlayExtra = SleepDownDesignTokens.SecondaryPage.TopOverlayExtra
private val DetailContentTopGap = SleepDownDesignTokens.SecondaryPage.ContentTopGap
internal val HomeInitialTopInset = 122.dp

@Composable
private fun HomeBackgroundBlurLayer(
    blurProgress: () -> Float,
    useFrozenHomeScene: () -> Boolean,
    closing: () -> Boolean,
    sceneKey: Any,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val frozenBlurLayer = rememberGraphicsLayer()
    val frozenRecordKey = remember { AtomicReference<Any?>(null) }
    val frozenRecordSize = remember { AtomicReference(IntSize.Zero) }
    val sampleScale = HomeFrozenBlurSampleScale
    val maximumBlurPx = with(density) { 12.dp.toPx() }
    val frozenBlurEffects = remember(maximumBlurPx, sampleScale) {
        List(HomeLiveBlurStepCount + 1) { index ->
            if (index == 0) null else {
                val radius = maximumBlurPx * sampleScale * index / HomeLiveBlurStepCount.toFloat()
                BlurEffect(radius, radius, TileMode.Clamp)
            }
        }
    }
    // The live path is kept for day view and per-frame personalization preview. Reuse a small
    // bounded set of RenderEffect instances so those exceptional paths no longer compile a new
    // full-screen blur effect for every animation frame.
    val liveBlurEffects = remember(maximumBlurPx) {
        List(HomeLiveBlurStepCount + 1) { index ->
            if (index == 0) null else {
                val radius = maximumBlurPx * index / HomeLiveBlurStepCount.toFloat()
                BlurEffect(radius, radius, TileMode.Clamp)
            }
        }
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                val frozenHomeScene = useFrozenHomeScene()
                val progress = blurProgress().coerceIn(0f, 1f)
                val isClosing = closing()
                val step = quantizeHomeBackgroundBlurStep(progress, isClosing)
                val fullResolutionClosingBlur = shouldUseFullResolutionClosingBlur(
                    frozenHomeScene = frozenHomeScene,
                    closing = isClosing,
                    blurProgress = progress
                )
                if (frozenHomeScene && !fullResolutionClosingBlur) {
                    renderEffect = null
                } else {
                    renderEffect = liveBlurEffects[step]
                }
            }
            .drawWithContent {
                val frozenHomeScene = useFrozenHomeScene()
                if (!frozenHomeScene) {
                    drawContent()
                    return@drawWithContent
                }

                val progress = blurProgress().coerceIn(0f, 1f)
                val isClosing = closing()
                val step = quantizeHomeBackgroundBlurStep(progress, isClosing)
                val fullResolutionClosingBlur = shouldUseFullResolutionClosingBlur(
                    frozenHomeScene = true,
                    closing = isClosing,
                    blurProgress = progress
                )
                if (step == 0 || fullResolutionClosingBlur) {
                    // Opening remains exclusively on the quarter-area frozen layer. Closing moves
                    // back to the already-recorded full-resolution home while several dp of blur
                    // still conceal the resolution handoff, so the final clear frame no longer
                    // swaps both blur strength and source resolution at once.
                    drawContent()
                    return@drawWithContent
                }

                val reducedSize = IntSize(
                    width = (size.width * sampleScale).roundToInt().coerceAtLeast(1),
                    height = (size.height * sampleScale).roundToInt().coerceAtLeast(1)
                )
                // The week tree below is already a stable GPU GraphicsLayer. Record only when its
                // semantic frame or window size changes; motion frames merely draw two textures.
                // This mirrors Android's own Kawase pipeline: blur a downsampled surface, then
                // upscale it. No Bitmap/ImageBitmap snapshot or CPU readback is involved.
                if (
                    frozenRecordKey.get() != sceneKey ||
                    frozenRecordSize.get() != reducedSize
                ) {
                    frozenBlurLayer.record(size = reducedSize) {
                        withTransform({
                            scale(
                                scaleX = sampleScale,
                                scaleY = sampleScale,
                                pivot = Offset.Zero
                            )
                        }) {
                            this@drawWithContent.drawContent()
                        }
                    }
                    frozenBlurLayer.pivotOffset = Offset.Zero
                    frozenBlurLayer.scaleX = 1f / sampleScale
                    frozenBlurLayer.scaleY = 1f / sampleScale
                    frozenRecordKey.set(sceneKey)
                    frozenRecordSize.set(reducedSize)
                }

                frozenBlurLayer.renderEffect = frozenBlurEffects[step]
                frozenBlurLayer.alpha = 1f
                drawLayer(frozenBlurLayer)
            },
        content = content
    )
}

@Composable
private fun rootTopBarLayoutHeight(screen: Screen): Dp {
    return when (screen) {
        Screen.Home -> rememberHomeAdaptiveMetrics().topOverlayHeight
        Screen.Config -> detailTopOverlayHeight()
    }
}

@Composable
internal fun detailTopOverlayHeight(): Dp {
    val density = LocalDensity.current
    val statusTop = with(density) { WindowInsets.safeDrawing.only(WindowInsetsSides.Top).getTop(this).toDp() }
    return statusTop + DetailTopBarHeight + DetailTopOverlayExtra
}

@Composable
private fun rootTopGradientHeight(screen: Screen): Dp {
    return when (screen) {
        Screen.Home -> rememberHomeAdaptiveMetrics().topGradientHeight
        Screen.Config -> detailTopOverlayHeight()
    }
}

@Composable
internal fun detailContentTopPadding(): Dp {
    LocalGlassSettingsContentTopPadding.current?.let { return it }
    val density = LocalDensity.current
    val statusTop = with(density) { WindowInsets.safeDrawing.only(WindowInsetsSides.Top).getTop(this).toDp() }
    return statusTop + DetailTopBarHeight + DetailContentTopGap
}

@Composable
fun DetailActivityScaffold(
    title: String,
    config: ScheduleConfigEntity,
    onBack: () -> Unit,
    showTopGradientBlur: Boolean = true,
    isolateContentFromBackdrop: Boolean = false,
    compactTopBar: Boolean = false,
    centerCompactTitle: Boolean = false,
    compactTitleMatchesSettings: Boolean = false,
    topBarVisible: Boolean = true,
    topBarActions: @Composable (Backdrop?) -> Unit = {},
    content: @Composable (Backdrop?) -> Unit
) {
    GlassMiuixDetailActivityScaffold(
        title = title,
        config = config,
        onBack = onBack,
        showTopGradientBlur = showTopGradientBlur,
        isolateContentFromBackdrop = isolateContentFromBackdrop,
        compactTopBar = compactTopBar,
        centerCompactTitle = centerCompactTitle,
        compactTitleMatchesSettings = compactTitleMatchesSettings,
        topBarVisible = topBarVisible,
        topBarActions = topBarActions,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    title: String,
    config: ScheduleConfigEntity,
    backdrop: Backdrop?,
    onBack: () -> Unit,
    centerTitle: Boolean = false,
    useMiuixCollapsedTitleStyle: Boolean = false,
    showBackButton: Boolean = true,
    backButtonStartPadding: Dp = 16.dp,
    actions: @Composable () -> Unit = {}
) {
    val density = LocalDensity.current
    val statusTop = with(density) { WindowInsets.safeDrawing.only(WindowInsetsSides.Top).getTop(this).toDp() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(statusTop + DetailTopBarHeight)
    ) {
        if (centerTitle) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DetailTopBarHeight)
                    .align(Alignment.BottomCenter)
            ) {
                if (showBackButton) {
                    TopBackButton(
                        backdrop = backdrop,
                        config = config,
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = backButtonStartPadding)
                            .size(SleepDownDesignTokens.SecondaryPage.BackButtonSize)
                    )
                }
                Text(
                    title,
                    style = if (useMiuixCollapsedTitleStyle) {
                        MiuixTheme.textStyles.title3
                    } else {
                        MaterialTheme.typography.titleLarge
                    },
                    fontWeight = if (useMiuixCollapsedTitleStyle) {
                        FontWeight.Medium
                    } else {
                        FontWeight.SemiBold
                    },
                    color = if (useMiuixCollapsedTitleStyle) {
                        MiuixTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    lineHeight = if (useMiuixCollapsedTitleStyle) {
                        MiuixTheme.textStyles.title3.fontSize
                    } else {
                        MaterialTheme.typography.titleLarge.fontSize
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.Center).padding(
                        horizontal = SleepDownDesignTokens.SecondaryPage.CenterTitleSideClearance
                    )
                )
            }
        } else Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailTopBarHeight)
                .align(Alignment.BottomCenter)
                .padding(start = backButtonStartPadding, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                TopBackButton(
                    backdrop = backdrop,
                    config = config,
                    onClick = onBack,
                    modifier = Modifier.size(SleepDownDesignTokens.SecondaryPage.BackButtonSize)
                )
            }
            Box(
                modifier = Modifier
                    .height(SleepDownDesignTokens.SecondaryPage.BackButtonSize)
                    .padding(start = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = MaterialTheme.typography.titleLarge.fontSize,
                    textAlign = TextAlign.Center
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .height(DetailTopBarHeight)
                .padding(end = 10.dp),
            contentAlignment = Alignment.CenterEnd
        ) { actions() }
    }
}

@Composable
fun settingsPageBackground(config: ScheduleConfigEntity): ComposeColor {
    return if (appUsesDarkTheme(config)) ComposeColor.Black else ComposeColor(0xFFEDEEF3)
}

@Composable
fun settingsVisualConfig(config: ScheduleConfigEntity): ScheduleConfigEntity {
    return config.copy(
        wallpaperUri = null,
        wallpaperBrightness = 1f,
        homeTextLight = appUsesDarkTheme(config),
        defaultWallpaperStyle = DefaultWallpaperStyle.NONE
    )
}

@Composable
fun HomeTopGradientBlur(
    config: ScheduleConfigEntity,
    backdrop: Backdrop?,
    modifier: Modifier = Modifier,
    height: Dp = HomeTopOverlayHeight
) {
    val lightGlass = glassUsesLightStyle(config)
    val tintColor = if (lightGlass) HomeLightGlassGradientColor else ComposeColor(0xFF111111)
    ProgressiveBackdropBlur(
        backdrop = backdrop,
        modifier = modifier,
        tintColor = tintColor,
        height = height,
        blurRadius = 12.dp,
        tintIntensity = if (lightGlass) 0.15f else 0.18f,
        direction = ProgressiveBlurDirection.TopToBottom,
        fallbackTintStops = listOf(
            0f to tintColor.copy(alpha = if (lightGlass) 0.34f else 0.42f),
            0.42f to tintColor.copy(alpha = if (lightGlass) 0.14f else 0.18f),
            1f to ComposeColor.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppTopBar(
    screen: Screen,
    state: AppState,
    settingsPage: SettingsPage,
    backdrop: Backdrop?,
    homeDisplayDate: LocalDate,
    homeDisplayWeek: Int,
    beforeScheduleTerm: Boolean,
    afterScheduleTerm: Boolean,
    homeShowingAnotherWeek: Boolean,
    onReturnHomeToCurrentWeek: () -> Unit,
    activeHomeOverlay: HomeAnchoredOverlayKind?,
    addButtonHidden: Boolean,
    onAddButtonPositioned: (androidx.compose.ui.geometry.Rect) -> Unit,
    onPersonalizeButtonPositioned: (androidx.compose.ui.geometry.Rect) -> Unit,
    onToggleAddMenu: (Float) -> Unit,
    onTogglePersonalize: (Float) -> Unit,
    onBackHome: () -> Unit
) {
    val adaptiveTopBarColor = LocalAdaptiveGlass.current.contentColor
    val homeTextColor = adaptiveTopBarColor
    TopAppBar(
        modifier = Modifier.statusBarsPadding().height(66.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ComposeColor.Transparent,
            scrolledContainerColor = ComposeColor.Transparent,
            titleContentColor = if (screen is Screen.Home) homeTextColor else MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = if (screen is Screen.Home) homeTextColor else MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = if (screen is Screen.Home) homeTextColor else MaterialTheme.colorScheme.onBackground
        ),
        title = {
            if (screen is Screen.Home) {
                HomeDateTitle(
                    state = state,
                    displayDate = homeDisplayDate,
                    displayWeek = homeDisplayWeek,
                    beforeScheduleTerm = beforeScheduleTerm,
                    afterScheduleTerm = afterScheduleTerm,
                    showReturnToCurrentWeekHint = homeShowingAnotherWeek,
                    onReturnCurrent = onReturnHomeToCurrentWeek
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        when (screen) {
                            Screen.Home -> ""
                            Screen.Config -> when (settingsPage) {
                                SettingsPage.Root -> "设置"
                                SettingsPage.General -> "通用设置"
                                SettingsPage.LiquidGlass -> "液态玻璃"
                                SettingsPage.Widgets -> "小组件设置"
                                SettingsPage.DayAgent -> "今日助手"
                                SettingsPage.Schedule -> "课表详细设置"
                                SettingsPage.Notifications -> "通知设置"
                                SettingsPage.ScheduleManager -> "课表设置"
                                SettingsPage.BackupRestore -> "备份与恢复"
                                SettingsPage.BackupPreview -> "恢复预览"
                                SettingsPage.About -> "关于应用"
                                SettingsPage.Changelog -> "关于应用"
								SettingsPage.PrivacyPolicy -> "隐私政策"
                            }
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 28.sp
                    )
                }
            }
        },
        navigationIcon = {
            if (screen is Screen.Config && settingsPage != SettingsPage.Root) {
                TopBackButton(
                    backdrop = backdrop,
                    config = state.config,
                    onClick = onBackHome,
                    modifier = Modifier.padding(start = 16.dp).size(42.dp)
                )
            }
        },
        actions = {
            if (screen is Screen.Home) {
                Row(
                    modifier = Modifier.padding(top = 2.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeIconButton(
                        backdrop = backdrop,
                        config = state.config,
                        iconRes = R.drawable.ic_edit,
                        contentDescription = "benchmark_personalize_button",
                        selected = false,
                        visible = activeHomeOverlay != HomeAnchoredOverlayKind.Personalize,
                        onClick = onTogglePersonalize,
                        onButtonPositioned = onPersonalizeButtonPositioned
                    )
                    HomeIconButton(
                        backdrop = backdrop,
                        config = state.config,
                        iconRes = R.drawable.ic_more_horizontal,
                        contentDescription = "添加菜单",
                        selected = false,
                        visible = !addButtonHidden,
                        onClick = onToggleAddMenu,
                        onButtonPositioned = onAddButtonPositioned
                    )
                }
            }
        }
    )
}

private fun popEnterTransition(): EnterTransition =
    fadeIn(animationSpec = spring(dampingRatio = 0.82f, stiffness = 520f)) +
        scaleIn(initialScale = 0.88f, animationSpec = spring(dampingRatio = 0.62f, stiffness = 520f))

private fun popExitTransition(): ExitTransition =
    fadeOut(animationSpec = spring(dampingRatio = 0.92f, stiffness = 560f)) +
        scaleOut(targetScale = 0.94f, animationSpec = spring(dampingRatio = 0.82f, stiffness = 560f))

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun CourseBoundsSource(
    courseId: Long,
    visible: Boolean,
    sharedScope: SharedTransitionScope?,
    shape: Shape,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    content(
        modifier.graphicsLayer {
            alpha = if (visible) 1f else 0f
        }
    )
}

@Composable
fun TopBackButton(backdrop: Backdrop?, config: ScheduleConfigEntity, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TopGlassIconButton(
        backdrop = backdrop,
        config = config,
        iconRes = R.drawable.ic_arrow_back,
        contentDescription = "返回",
        onClick = onClick,
        modifier = modifier,
        buttonHeight = 42.dp
    )
}

@Composable
fun TopGlassIconButton(
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    surfaceColorOverride: ComposeColor? = null,
    buttonHeight: Dp = 42.dp
) {
    val lightGlass = glassUsesLightStyle(config)
    if (backdrop != null) {
        LiquidButton(
            onClick = onClick,
            backdrop = backdrop,
            modifier = modifier,
            height = buttonHeight,
            surfaceColor = surfaceColorOverride ?: if (lightGlass) {
                ComposeColor.White.copy(alpha = 0.26f)
            } else {
                ComposeColor(0xFF121212).copy(alpha = 0.28f)
            },
            contentPadding = PaddingValues(0.dp),
            blurRadius = 3.dp,
            lensHeight = 16.dp,
            lensAmount = 24.dp,
            chromaticAberration = false,
            shadowEnabled = false
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(painterResource(iconRes), contentDescription = contentDescription, modifier = Modifier.size(22.dp))
            }
        }
    } else {
        GlassPill(
            backdrop = null,
            config = config,
            modifier = modifier,
            onClick = onClick
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(painterResource(iconRes), contentDescription = contentDescription, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun HomeActionCapsule(
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    homeMode: HomeMode,
    visible: Boolean,
    onPersonalize: (Float) -> Unit,
    onMore: (Float) -> Unit,
    onCapsulePositioned: (androidx.compose.ui.geometry.Rect) -> Unit
) {
    val view = LocalView.current
    val pressSnapshot = remember { LiquidButtonPressSnapshot() }
    fun capturedScale(): Float = 1f + (3f / 42f) * pressSnapshot.progress.coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            // Keep the visual right edge on the same inset as the Today Agent surface. Width
            // changes therefore trim only the leading side instead of shifting the shared edge.
            .padding(end = if (homeMode == HomeMode.Week) 4.dp else 7.dp)
            .size(width = 100.dp, height = 42.dp)
            .onGloballyPositioned { onCapsulePositioned(it.boundsInRoot()) }
            .graphicsLayer { alpha = if (visible) 1f else 0f }
    ) {
        HomeActionCapsuleVisual(
            backdrop = backdrop,
            config = config,
            modifier = Modifier.fillMaxSize(),
            isInteractive = visible,
            pressSnapshot = pressSnapshot,
            onPersonalize = {
                performButtonHaptic(view)
                onPersonalize(capturedScale())
            },
            onMore = {
                performButtonHaptic(view)
                onMore(capturedScale())
            }
        )
    }
}

@Composable
internal fun HomeActionCapsuleVisual(
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true,
    pressSnapshot: LiquidButtonPressSnapshot? = null,
    onPersonalize: () -> Unit = {},
    onMore: () -> Unit = {}
) {
    val adaptiveGlass = LocalAdaptiveGlass.current
    val lightGlass = adaptiveGlass.lightGlass
    val personalizeInteraction = remember { MutableInteractionSource() }
    val moreInteraction = remember { MutableInteractionSource() }
    val interactionModifier: (String, MutableInteractionSource, () -> Unit) -> Modifier =
        { description, interaction, action ->
        Modifier
            .fillMaxHeight()
            .semantics { contentDescription = description }
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = isInteractive,
                onClick = action
            )
    }
    val content: @Composable () -> Unit = {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = interactionModifier(
                    "benchmark_personalize_button",
                    personalizeInteraction,
                    onPersonalize
                ).let { Modifier.weight(1f).then(it) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_edit),
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                    tint = adaptiveGlass.contentColor
                )
            }
            Box(
                modifier = interactionModifier("添加菜单", moreInteraction, onMore)
                    .let { Modifier.weight(1f).then(it) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_more_horizontal),
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                    tint = adaptiveGlass.contentColor
                )
            }
        }
    }
    if (backdrop != null) {
        LiquidButton(
            onClick = {},
            backdrop = backdrop,
            modifier = modifier,
            isInteractive = isInteractive,
            clickTargetEnabled = false,
            height = 42.dp,
            contentPadding = PaddingValues(0.dp),
            blurRadius = homeChromeBlur(HomeHeaderGlassBlur, config),
            lensHeight = HomeHeaderGlassLensHeight,
            lensAmount = HomeHeaderGlassLensAmount,
            chromaticAberration = false,
            shadowEnabled = false,
            pressExpansion = 3.dp,
            highlightRadiusMultiplier = 0.9f,
            pressSnapshot = pressSnapshot,
            surfaceColor = (
                if (lightGlass) HomeLightGlassSurfaceColor else ComposeColor(0xFF121212)
                ).copy(alpha = homeChromeGlassSurfaceAlpha(lightGlass))
        ) { content() }
    } else {
        GlassPill(backdrop = null, config = config, modifier = modifier) { content() }
    }
}

@Composable
fun HomeIconButton(
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    iconRes: Int,
    contentDescription: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    accentColor: ComposeColor = ComposeColor.Unspecified,
    visible: Boolean = true,
    onClick: (Float) -> Unit,
    onButtonPositioned: ((androidx.compose.ui.geometry.Rect) -> Unit)? = null
) {
    val view = LocalView.current
    val pressSnapshot = remember { LiquidButtonPressSnapshot() }
    fun capturedScale(): Float = 1f + (3f / 42f) * pressSnapshot.progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .padding(end = 7.dp)
            .size(42.dp)
            .then(
                if (onButtonPositioned != null) {
                    Modifier.onGloballyPositioned { onButtonPositioned(it.boundsInRoot()) }
                } else {
                    Modifier
                }
            )
            .graphicsLayer { alpha = if (visible) 1f else 0f }
    ) {
        HomeIconButtonVisual(
            backdrop = backdrop,
            config = config,
            iconRes = iconRes,
            contentDescription = contentDescription,
            selected = selected,
            accentColor = accentColor,
            modifier = Modifier.fillMaxSize(),
            isInteractive = visible,
            pressSnapshot = pressSnapshot,
            onClick = {
                performButtonHaptic(view)
                onClick(capturedScale())
            }
        )
    }
}

@Composable
internal fun HomeIconButtonVisual(
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    iconRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    accentColor: ComposeColor = ComposeColor.Unspecified,
    isInteractive: Boolean = true,
    pressSnapshot: LiquidButtonPressSnapshot? = null,
    onClick: () -> Unit = {}
) {
    val adaptiveGlass = LocalAdaptiveGlass.current
    val lightGlass = adaptiveGlass.lightGlass
    val baseSurfaceColor = if (lightGlass) HomeLightGlassSurfaceColor else ComposeColor(0xFF121212)
    val buttonSurfaceColor = if (accentColor.isSpecified) {
        accentColor.copy(alpha = if (lightGlass) HomeLightGlassAccentTintAlpha else 0.32f)
    } else {
        baseSurfaceColor.copy(alpha = homeChromeGlassSurfaceAlpha(lightGlass))
    }
    if (backdrop != null) {
        LiquidButton(
            onClick = if (isInteractive) onClick else ({}),
            backdrop = backdrop,
            modifier = modifier,
            height = 42.dp,
            tint = if (accentColor.isSpecified) accentColor else ComposeColor.Unspecified,
            surfaceColor = buttonSurfaceColor,
            contentPadding = PaddingValues(0.dp),
            blurRadius = homeChromeBlur(HomeHeaderGlassBlur, config),
            lensHeight = HomeHeaderGlassLensHeight,
            lensAmount = HomeHeaderGlassLensAmount,
            chromaticAberration = false,
            shadowEnabled = false,
            pressExpansion = 3.dp,
            pressSnapshot = pressSnapshot
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    painterResource(iconRes),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(20.dp),
                    tint = adaptiveGlass.contentColor
                )
            }
        }
    } else {
        GlassPill(
            backdrop = null,
            config = config,
            modifier = modifier,
            selected = selected,
            onClick = if (isInteractive) onClick else ({})
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(painterResource(iconRes), contentDescription = contentDescription, modifier = Modifier.size(20.dp))
            }
        }
    }
}

data class AddMenuAction(
    val iconRes: Int,
    val label: String,
    val onClick: () -> Unit
)

private fun Bitmap.cropToAnchoredBounds(bounds: Rect): Bitmap? = runCatching {
    val left = bounds.left.roundToInt().coerceIn(0, width - 1)
    val top = bounds.top.roundToInt().coerceIn(0, height - 1)
    val cropWidth = bounds.width.roundToInt().coerceIn(1, width - left)
    val cropHeight = bounds.height.roundToInt().coerceIn(1, height - top)
    Bitmap.createBitmap(this, left, top, cropWidth, cropHeight)
}.getOrNull()

@Composable
fun AddMenuLiquidItem(
    config: ScheduleConfigEntity,
    action: AddMenuAction,
    itemHeight: Dp,
    highlighted: Boolean
) {
    val baseText = glassForegroundColor(config)
    val selectedColor = ComposeColor(0xFF8E8E93).copy(
        alpha = if (glassUsesLightStyle(config)) 0.20f else 0.28f
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight - 2.dp)
                .clip(RoundedCornerShape(HomeAddMenuSelectionCornerDp.dp))
                .background(if (highlighted) selectedColor else ComposeColor.Transparent)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painterResource(action.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                    tint = baseText
                )
                Text(
                    action.label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = baseText,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FloatingDock(
    selected: Screen,
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    homeMode: HomeMode,
    modifier: Modifier = Modifier,
    previewMode: Boolean = false,
    onHomeModeChange: (HomeMode) -> Unit,
    onHome: () -> Unit,
    onConfig: () -> Unit
) {
    val adaptiveGlass = LocalAdaptiveGlass.current
    val lightGlass = if (selected is Screen.Home) {
        adaptiveGlass.lightGlass
    } else {
        !appUsesDarkTheme(config)
    }
    val density = LocalDensity.current
    // MIUI reports the IME-sized bottom edge through safeDrawing for the underlying Activity
    // while a transparent Compose Dialog owns the keyboard. That makes a bottom-aligned Dock jump
    // above the keyboard even though the Activity itself uses adjustNothing. Use the stable
    // navigation-bar inset as the physical screen edge; the separate IME compensation still
    // handles devices that genuinely resize the Activity window.
    val systemBottomPx = if (previewMode) {
        0
    } else {
        WindowInsets.navigationBarsIgnoringVisibility
            .only(WindowInsetsSides.Bottom)
            .getBottom(density)
    }
    val bottomInset = with(density) { systemBottomPx.toDp() }
    val imeCompensationPx = dockImeCompensationPx(
        imeBottomPx = if (previewMode) 0 else WindowInsets.ime.getBottom(density),
        systemBottomPx = systemBottomPx
    )
    val bottomOffset = (bottomInset + 8.dp).coerceAtLeast(8.dp)
    val dockTextColor by animateColorAsState(
        targetValue = if (selected is Screen.Home) {
            adaptiveGlass.contentColor
        } else if (appUsesDarkTheme(config)) {
            ComposeColor.White
        } else {
            ComposeColor.Black
        },
        animationSpec = tween(220),
        label = "FloatingDockContentTheme"
    )
    val dockAlignment = when (config.dockAlignment) {
        DockAlignment.LEFT -> Alignment.BottomStart
        DockAlignment.CENTER -> Alignment.BottomCenter
        DockAlignment.RIGHT -> Alignment.BottomEnd
    }
    val horizontalPadding = when (config.dockAlignment) {
        DockAlignment.LEFT -> PaddingValues(start = 18.dp, bottom = bottomOffset)
        DockAlignment.CENTER -> PaddingValues(start = 18.dp, end = 18.dp, bottom = bottomOffset)
        DockAlignment.RIGHT -> PaddingValues(end = 18.dp, bottom = bottomOffset)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontalPadding)
            // MainActivity keeps adjustResize for text fields. Counter only the IME portion
            // here so the home dock stays at its physical bottom position behind the keyboard.
            .graphicsLayer { translationY = imeCompensationPx.toFloat() },
        contentAlignment = dockAlignment
    ) {
        if (backdrop != null) {
            CompositionLocalProvider(LocalContentColor provides dockTextColor) {
                LiquidBottomTabs(
                    selectedTabIndex = {
                        when {
                            selected !is Screen.Home -> 2
                            homeMode == HomeMode.Day -> 0
                            else -> 1
                        }
                    },
                    onTabSelected = { index ->
                        when (index) {
                            0 -> {
                                onHomeModeChange(HomeMode.Day)
                                onHome()
                            }
                            1 -> {
                                onHomeModeChange(HomeMode.Week)
                                onHome()
                            }
                            else -> onConfig()
                        }
                    },
                    backdrop = backdrop,
                    tabsCount = 3,
                    modifier = Modifier.width(210.dp),
                    containerHeight = 54.dp,
                    indicatorHeight = 46.dp,
                    blurRadius = homeChromeBlur(1.3.dp, config),
                    containerAlpha = homeChromeGlassSurfaceAlpha(lightGlass),
                    lensHeight = 10.dp,
                    lensAmount = 40.dp,
                    indicatorWidthOverflow = 8.dp,
                    indicatorHeightOverflow = 4.dp,
                    indicatorLensHeight = 12.dp,
                    indicatorLensAmount = 17.dp,
                    officialHighlightAlpha = 0.07f,
                    officialShadowAlpha = 0.05f,
                    officialInnerShadowAlpha = 0.08f,
                    // 色差效果会抬高每帧渲染成本；dock 上关闭以减少滑动时掉帧。
                    chromaticAberrationEnabled = false,
                    isLightThemeOverride = lightGlass,
                    lightContainerColor = HomeLightGlassSurfaceColor,
                    lightAccentColor = HomeLightGlassSelectedAccentColor,
                    useOfficialGlassParameters = true
                ) {
                    LiquidBottomTab(onClick = {
                        onHomeModeChange(HomeMode.Day)
                        onHome()
                    }) {
                        DockTabContent(R.drawable.ic_day_view, "课表", iconSize = 23.dp)
                    }
                    LiquidBottomTab(onClick = {
                        onHomeModeChange(HomeMode.Week)
                        onHome()
                    }) {
                        DockTabContent(R.drawable.ic_week_view, "周课表", iconSize = 23.dp)
                    }
                    LiquidBottomTab(onClick = onConfig) {
                        DockTabContent(R.drawable.ic_settings, "设置", iconSize = 24.dp)
                    }
                }
            }
        } else {
            GlassPill(backdrop = null, config = config, modifier = Modifier.width(210.dp)) {
                Row(modifier = Modifier.height(54.dp).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    DockItem(
                        selected is Screen.Home && homeMode == HomeMode.Day,
                        null,
                        config,
                        R.drawable.ic_day_view,
                        "课表",
                        onClick = {
                            onHomeModeChange(HomeMode.Day)
                            onHome()
                        }
                    )
                    DockItem(
                        selected is Screen.Home && homeMode == HomeMode.Week,
                        null,
                        config,
                        R.drawable.ic_week_view,
                        "周课表",
                        onClick = {
                            onHomeModeChange(HomeMode.Week)
                            onHome()
                        }
                    )
                    DockItem(selected is Screen.Config, null, config, R.drawable.ic_settings, "设置", onConfig)
                }
            }
        }
    }
}

@Composable
fun ScheduleManagerEntryPill(
    visible: Boolean,
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(enabled = visible) {
        onDismiss()
    }
    LaunchedEffect(visible) {
        if (visible) {
            delay(5_000)
            onDismiss()
        }
    }
    val density = LocalDensity.current
    val bottomInset = with(density) { WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).getBottom(this).toDp() }
    val dockBottomOffset = (bottomInset + 8.dp).coerceAtLeast(8.dp)
    val pillBottomOffset = dockBottomOffset + 68.dp
    val dockAlignment = when (config.dockAlignment) {
        DockAlignment.LEFT -> Alignment.BottomStart
        DockAlignment.CENTER -> Alignment.BottomCenter
        DockAlignment.RIGHT -> Alignment.BottomEnd
    }
    val horizontalPadding = when (config.dockAlignment) {
        DockAlignment.LEFT -> PaddingValues(start = 18.dp, bottom = pillBottomOffset)
        DockAlignment.CENTER -> PaddingValues(start = 18.dp, end = 18.dp, bottom = pillBottomOffset)
        DockAlignment.RIGHT -> PaddingValues(end = 18.dp, bottom = pillBottomOffset)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontalPadding),
        contentAlignment = dockAlignment
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(320)) +
                scaleIn(initialScale = 0.4f, animationSpec = tween(320, easing = CubicBezierEasing(0.2f, 0.75f, 0.2f, 1f))),
            exit = fadeOut(animationSpec = tween(120)) +
                scaleOut(targetScale = 0.4f, animationSpec = tween(120))
        ) {
            val lightGlass = glassUsesLightStyle(config)
            if (backdrop != null) {
                LiquidButton(
                    onClick = onClick,
                    backdrop = backdrop,
                    modifier = Modifier.width(140.dp),
                    height = 48.dp,
                    blurRadius = 2.dp,
                    lensHeight = 12.dp,
                    lensAmount = 24.dp,
                        surfaceColor = if (lightGlass) HomeLightGlassSurfaceColor.copy(alpha = 0.075f) else ComposeColor(0xFF121212).copy(alpha = 0.075f),
                        chromaticAberration = false,
                        shadowEnabled = false,
                        highlightEnabled = true,
                        contentPadding = PaddingValues(horizontal = 18.dp)
                ) {
                    Text(
                        "课表设置",
                        color = glassForegroundColor(config),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            } else {
                GlassPill(
                    backdrop = null,
                    config = config,
                    modifier = Modifier
                        .width(140.dp)
                        .height(48.dp),
                    onClick = onClick
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "课表设置",
                            color = glassForegroundColor(config),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DockBackdropContinuityPatch(config: ScheduleConfigEntity, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val bottomInset = with(density) { WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).getBottom(this).toDp() }
    val height = (bottomInset + 92.dp).coerceAtLeast(92.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(ComposeColor.Transparent)
    )
}

@Composable
private fun DockTabContent(iconRes: Int, label: String, iconSize: Dp) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(ComposeColor.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
    ) {
        Icon(painterResource(iconRes), contentDescription = null, modifier = Modifier.size(iconSize))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

internal const val PersonalizeWallpaperBlurSlider = "wallpaper-blur"
internal const val PersonalizeWallpaperBrightnessSlider = "wallpaper-brightness"
private const val PersonalizeWallpaperContentChange = "wallpaper-content"
private const val PersonalizeWeekHeightSlider = "week-height"
internal const val PersonalizeWeekCornerSlider = "week-corner"
private const val PersonalizeCardColorChange = "card-color"
internal const val PersonalizeCardAlphaSlider = "card-alpha"
internal const val PersonalizeCardBlurSlider = "card-blur"
internal const val PersonalizeCardFontSlider = "card-font"
private const val PersonalizeCardGlassChange = "card-glass"

internal fun resolveActivePersonalizationSlider(
    currentKey: String?,
    eventKey: String,
    active: Boolean
): String? = when {
    active -> eventKey
    currentKey == eventKey -> null
    else -> currentKey
}

internal fun mergePersonalizationCandidate(
    current: ScheduleConfigEntity,
    candidate: ScheduleConfigEntity,
    changeKey: String
): ScheduleConfigEntity = when (changeKey) {
    PersonalizeWallpaperContentChange -> current.copy(
        wallpaperUri = candidate.wallpaperUri,
        wallpaperPortraitCenterX = candidate.wallpaperPortraitCenterX,
        wallpaperPortraitCenterY = candidate.wallpaperPortraitCenterY,
        wallpaperPortraitScale = candidate.wallpaperPortraitScale,
        wallpaperLandscapeCenterX = candidate.wallpaperLandscapeCenterX,
        wallpaperLandscapeCenterY = candidate.wallpaperLandscapeCenterY,
        wallpaperLandscapeScale = candidate.wallpaperLandscapeScale,
        wallpaperSourceWidth = candidate.wallpaperSourceWidth,
        wallpaperSourceHeight = candidate.wallpaperSourceHeight,
        homeTextLight = candidate.homeTextLight
    )
    PersonalizeWallpaperBlurSlider -> current.copy(wallpaperBlur = candidate.wallpaperBlur)
    PersonalizeWallpaperBrightnessSlider -> current.copy(
        wallpaperBrightness = candidate.wallpaperBrightness
    )
    PersonalizeWeekHeightSlider -> current.copy(
        weekCardHeightDp = null,
        weekCardHeightScale = candidate.weekCardHeightScale
    )
    PersonalizeWeekCornerSlider -> current.copy(
        weekCardCornerProgress = candidate.weekCardCornerProgress
    )
    PersonalizeCardColorChange -> current.copy(
        cardColorArgb = candidate.cardColorArgb,
        courseCardColorMode = candidate.courseCardColorMode,
        courseCardPalette = if (candidate.courseCardColorMode == CourseCardColorMode.COLORFUL) {
            candidate.courseCardPalette
        } else {
            current.courseCardPalette
        }
    )
    PersonalizeCardAlphaSlider -> current.copy(cardAlpha = candidate.cardAlpha)
    PersonalizeCardBlurSlider -> current.copy(courseCardBlur = candidate.courseCardBlur)
    PersonalizeCardFontSlider -> current.copy(courseCardFontScale = candidate.courseCardFontScale)
    PersonalizeCardGlassChange -> current.switchCourseCardGlassMode(
        candidate.courseCardGlassEnabled
    )
    else -> current
}

private fun Modifier.personalizePreviewVisibility(
    activeKey: String?,
    previewProgress: Float,
    ownKey: String? = null
): Modifier {
    val selectedSlider = activeKey != null && activeKey == ownKey
    val effectiveAlpha = if (selectedSlider) 1f else 1f - previewProgress.coerceIn(0f, 1f)
    return graphicsLayer { alpha = effectiveAlpha }
        .drawWithContent {
            // Alpha alone does not guarantee nested Liquid controls skip backdrop work. Keep the
            // measured slot intact, but stop their draw traversal once they are truly invisible.
            if (effectiveAlpha > 0.005f) drawContent()
        }
}

@Composable
private fun PersonalizeSliderLabel(
    value: State<Float>,
    label: (Float) -> String
) {
    Text(label(value.value), style = MaterialTheme.typography.labelMedium)
}

@Composable
private fun PersonalizeValueSlider(
    sliderKey: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    backdrop: Backdrop?,
    label: (Float) -> String,
    onCommit: (Float) -> Unit,
    onPreviewValueChange: (Float) -> Unit,
    previewSliderKey: String?,
    previewProgress: Float,
    onSliderPreviewActiveChange: (String, Boolean) -> Unit,
    snapValue: Float? = null,
    benchmarkDescription: String? = null,
    onTouchActiveChange: (Boolean) -> Unit = {}
) {
    val displayValue = remember(valueRange) { mutableFloatStateOf(value.coerceIn(valueRange)) }
    LaunchedEffect(value, valueRange) {
        displayValue.floatValue = value.coerceIn(valueRange)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .personalizePreviewVisibility(previewSliderKey, previewProgress, sliderKey),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PersonalizeSliderLabel(displayValue, label)
        LiquidControlSlider(
            value = value,
            onValueChange = onCommit,
            valueRange = valueRange,
            backdrop = backdrop,
            onLiveValueChange = {
                displayValue.floatValue = it
                onPreviewValueChange(it)
            },
            snapValue = snapValue,
            modifier = if (benchmarkDescription == null) Modifier else Modifier.semantics {
                contentDescription = benchmarkDescription
            },
            onSliderTouchActiveChange = { active ->
                onTouchActiveChange(active)
                onSliderPreviewActiveChange(sliderKey, active)
            }
        )
    }
}

@Composable
private fun WallpaperBlurControl(
    blurDp: Float,
    backdrop: Backdrop?,
    onCommit: (Float) -> Unit,
    onPreviewValueChange: (Float) -> Unit,
    previewSliderKey: String?,
    previewProgress: Float,
    onSliderPreviewActiveChange: (String, Boolean) -> Unit,
    onTouchActiveChange: (Boolean) -> Unit
) {
    PersonalizeValueSlider(
        sliderKey = PersonalizeWallpaperBlurSlider,
        value = wallpaperBlurPercent(blurDp),
        valueRange = 0f..100f,
        backdrop = backdrop,
        label = { "壁纸模糊 ${it.roundToInt()}%" },
        onCommit = onCommit,
        onPreviewValueChange = onPreviewValueChange,
        previewSliderKey = previewSliderKey,
        previewProgress = previewProgress,
        onSliderPreviewActiveChange = onSliderPreviewActiveChange,
        snapValue = 0f,
        benchmarkDescription = "benchmark_personalization_blur_slider",
        onTouchActiveChange = onTouchActiveChange
    )
}

private enum class CourseColorDialogStage { Picker, Wallpaper }

private val SolidCourseColorPresets = listOf(
    0xFFD6E9FFL,
    0xFFFFC4D6L,
    0xFFB7E4C7L,
    0xFFFFD166L
)

private val GradientCourseColorPresets = listOf(
    0xFF79BDF2L,
    0xFFA99AE8L,
    0xFFF09AB6L,
    0xFF71C8A1L
)

/** One affordance for the original wallpaper-driven automatic colourful mode. */
private val AutomaticColorfulCoursePreview = DefaultCourseCardPalette.take(4)

private fun vividPreviewColor(argb: Long, minimumValue: Float = 0.72f): ComposeColor {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(argb.toInt(), hsv)
    hsv[1] = hsv[1].coerceIn(0.34f, 0.82f)
    hsv[2] = hsv[2].coerceIn(minimumValue, 0.96f)
    return ComposeColor(android.graphics.Color.HSVToColor(hsv))
}

private fun tonalPreviewColors(seed: Long): List<ComposeColor> {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(seed.toInt(), hsv)
    val hue = hsv[0]
    val saturation = hsv[1].coerceAtLeast(0.40f)
    return listOf(
        ComposeColor(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation * 0.34f, 0.98f))),
        ComposeColor(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation * 0.58f, 0.90f))),
        ComposeColor(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation * 0.78f, 0.80f))),
        ComposeColor(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation.coerceAtMost(0.88f), 0.70f)))
    )
}

@Composable
private fun CourseColorModeRow(
    title: String,
    mode: CourseCardColorMode,
    selectedMode: CourseCardColorMode,
    presets: List<List<Long>>,
    selectedPreset: (List<Long>) -> Boolean,
    onPresetSelected: (List<Long>) -> Unit,
    onOpenPalette: () -> Unit
) {
    val foreground = LocalContentColor.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.width(42.dp),
            color = if (selectedMode == mode) MaterialTheme.colorScheme.primary else foreground,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Medium
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = if (mode == CourseCardColorMode.COLORFUL) {
                Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)
            } else {
                Arrangement.SpaceEvenly
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            presets.forEach { colors ->
                val selected = selectedMode == mode && selectedPreset(colors)
                val previewColors = when (mode) {
                    CourseCardColorMode.SOLID -> colors.map { ComposeColor(it.toInt()) }
                    CourseCardColorMode.GRADIENT -> tonalPreviewColors(colors.first())
                    CourseCardColorMode.COLORFUL -> colors.map { vividPreviewColor(it) }
                }
                val shape = RoundedCornerShape(50)
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = shape,
                    color = ComposeColor.Transparent,
                    border = if (selected) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        null
                    },
                    onClick = { onPresetSelected(colors) }
                ) {
                    Box(
                        Modifier.background(
                            when {
                                previewColors.size == 1 -> Brush.linearGradient(
                                    listOf(
                                        previewColors.first(),
                                        previewColors.first()
                                    )
                                )
                                mode == CourseCardColorMode.COLORFUL -> Brush.sweepGradient(
                                    previewColors
                                )
                                else -> Brush.linearGradient(
                                    previewColors
                                )
                            }
                        )
                    )
                }
            }
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(50),
                color = ComposeColor.Transparent,
                border = null,
                onClick = onOpenPalette
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.sweepGradient(
                                AutomaticColorfulCoursePreview.map { vividPreviewColor(it) }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Palette,
                        contentDescription = "自定义课程卡颜色",
                        tint = if (foreground.luminance() > 0.5f) ComposeColor.White else ComposeColor.Black,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseColorEditorDialog(
    show: Boolean,
    stage: CourseColorDialogStage,
    mode: CourseCardColorMode,
    colors: List<Long>,
    selectedIndex: Int,
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    onDismiss: () -> Unit,
    onStageChange: (CourseColorDialogStage) -> Unit,
    onSelectedIndexChange: (Int) -> Unit,
    onColorChange: (Int, Long) -> Unit,
    onConfirm: () -> Unit
) {
    val foreground = appPanelForegroundColor(config)
    SleepDownPickerDialog(
        show = show,
        title = if (stage == CourseColorDialogStage.Picker) "选择颜色" else "从壁纸取色",
        onDismissRequest = onDismiss,
        backdrop = backdrop,
        config = config,
        contentSpacing = 14.dp,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
        blurRadius = 10.dp,
        contentTransitionKey = stage,
        titleAction = {
            if (stage == CourseColorDialogStage.Picker) {
                IconButton(
                    onClick = { onStageChange(CourseColorDialogStage.Wallpaper) },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Colorize,
                        contentDescription = "从壁纸吸色",
                        tint = foreground,
                        modifier = Modifier.size(21.dp)
                    )
                }
            } else {
                // Keep the title row's geometry stable while its content crossfades.
                Spacer(Modifier.size(38.dp))
            }
        }
    ) {
        if (stage == CourseColorDialogStage.Picker) {
            if (mode == CourseCardColorMode.COLORFUL && colors.size > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    colors.forEachIndexed { index, color ->
                        Surface(
                            modifier = Modifier.size(34.dp),
                            shape = RoundedCornerShape(50),
                            color = ComposeColor(color.toInt()),
                            border = BorderStroke(
                                if (index == selectedIndex) 3.dp else 1.dp,
                                if (index == selectedIndex) MaterialTheme.colorScheme.primary
                                else foreground.copy(alpha = 0.34f)
                            ),
                            onClick = { onSelectedIndexChange(index) }
                        ) {}
                    }
                }
            }
            top.yukonga.miuix.kmp.basic.ColorPalette(
                color = ComposeColor(colors.getOrElse(selectedIndex) { colors.firstOrNull() ?: 0xFFD6E9FFL }.toInt()),
                onColorChanged = { color ->
                    onColorChange(selectedIndex, color.copy(alpha = 1f).toArgb().toLong() and 0xFFFFFFFFL)
                },
                modifier = Modifier.fillMaxWidth(),
                rows = 7,
                hueColumns = 12,
                includeGrayColumn = true,
                showPreview = true,
                cornerRadius = 16.dp,
                indicatorRadius = 10.dp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickSheetLiquidAction(
                    label = "取消",
                    enabled = true,
                    backdrop = backdrop,
                    config = config,
                    modifier = Modifier.weight(1f),
                    height = SleepDownDesignTokens.CenteredDialog.ActionHeight,
                    onClick = onDismiss
                )
                QuickSheetLiquidAction(
                    label = "确认",
                    enabled = colors.isNotEmpty(),
                    backdrop = backdrop,
                    config = config,
                    modifier = Modifier.weight(1f),
                    primary = true,
                    height = SleepDownDesignTokens.CenteredDialog.ActionHeight,
                    onClick = onConfirm
                )
            }
        } else {
            WallpaperPaletteSampler(
                mode = mode,
                colors = colors,
                config = config,
                onColorChange = onColorChange,
                onCancel = { onStageChange(CourseColorDialogStage.Picker) },
                onConfirm = onConfirm,
                backdrop = backdrop
            )
        }
    }
}

@Composable
private fun WallpaperPaletteSampler(
    mode: CourseCardColorMode,
    colors: List<Long>,
    config: ScheduleConfigEntity,
    backdrop: Backdrop?,
    onColorChange: (Int, Long) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val windowHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    // Miuix caps a centered dialog at two thirds of the current window. Reserve the measured
    // title, copy, actions, shell insets and compact gaps first, then let the wallpaper preview
    // consume only the remaining space. This keeps both actions inside the shell on phones with
    // large display or font scaling while retaining a useful sampling area on taller screens.
    val previewHeight = (windowHeight * (2f / 3f) - 210.dp).coerceIn(180.dp, 300.dp)
    val foreground = appPanelForegroundColor(config)
    val useDarkDefaultWallpaper = appUsesDarkTheme(config)
    val bitmap by produceState<Bitmap?>(
        initialValue = null,
        config.wallpaperUri,
        config.defaultWallpaperStyle,
        useDarkDefaultWallpaper
    ) {
        value = withContext(Dispatchers.IO) {
            loadWallpaperBitmap(context, config, useDarkDefaultWallpaper)
        }
    }
    val pointCount = if (mode == CourseCardColorMode.COLORFUL) 4 else 1
    var samplePoints by remember(mode) {
        mutableStateOf(
            if (pointCount == 1) {
                listOf(Offset(0.50f, 0.50f))
            } else {
                listOf(
                    Offset(0.28f, 0.28f),
                    Offset(0.72f, 0.30f),
                    Offset(0.32f, 0.72f),
                    Offset(0.72f, 0.74f)
                )
            }
        )
    }
    var selectedPoint by remember(mode) { mutableIntStateOf(0) }
    var previewSize by remember { mutableStateOf(IntSize.Zero) }

    fun updatePoint(index: Int, point: Offset) {
        val safePoint = Offset(point.x.coerceIn(0.04f, 0.96f), point.y.coerceIn(0.04f, 0.96f))
        samplePoints = samplePoints.toMutableList().also { it[index] = safePoint }
        val source = bitmap ?: return
        val cropState = if (!config.wallpaperUri.isNullOrBlank()) {
            config.wallpaperCropState(WallpaperPreviewOrientation.Portrait)
        } else {
            WallpaperCropState()
        }
        sampleCroppedBitmapColor(
            source,
            previewSize,
            safePoint.x * previewSize.width,
            safePoint.y * previewSize.height,
            cropState
        )?.let { onColorChange(index, it) }
    }

    LaunchedEffect(bitmap, previewSize, mode) {
        val source = bitmap ?: return@LaunchedEffect
        if (previewSize.width <= 0 || previewSize.height <= 0) return@LaunchedEffect
        val cropState = if (!config.wallpaperUri.isNullOrBlank()) {
            config.wallpaperCropState(WallpaperPreviewOrientation.Portrait)
        } else {
            WallpaperCropState()
        }
        samplePoints.take(pointCount).forEachIndexed { index, point ->
            sampleCroppedBitmapColor(
                source,
                previewSize,
                point.x * previewSize.width,
                point.y * previewSize.height,
                cropState
            )?.let { onColorChange(index, it) }
        }
    }

    Text(
        text = if (mode == CourseCardColorMode.COLORFUL) {
            "拖动圆点，为彩色组合提取四个颜色"
        } else if (mode == CourseCardColorMode.GRADIENT) {
            "拖动圆点，提取渐变色系的基准色"
        } else {
            "拖动圆点，提取课程卡片颜色"
        },
        color = foreground.copy(alpha = 0.70f),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(210.dp)
                .height(previewHeight)
                .clip(RoundedCornerShape(24.dp))
                .background(if (appUsesDarkTheme(config)) ComposeColor(0xFF15171C) else ComposeColor(0xFFF1F3F8))
                .onSizeChanged { previewSize = it }
                .pointerInput(bitmap, previewSize, selectedPoint) {
                    detectTapGestures { tap ->
                        if (previewSize.width > 0 && previewSize.height > 0) {
                            updatePoint(
                                selectedPoint,
                                Offset(tap.x / previewSize.width, tap.y / previewSize.height)
                            )
                        }
                    }
                }
        ) {
            bitmap?.let { wallpaper ->
                FocusCroppedWallpaper(
                    bitmap = wallpaper,
                    config = config,
                    modifier = Modifier.fillMaxSize(),
                    useSavedCrop = !config.wallpaperUri.isNullOrBlank()
                )
            } ?: Text(
                text = "当前没有可取色的壁纸",
                color = foreground,
                modifier = Modifier.align(Alignment.Center)
            )
            samplePoints.forEachIndexed { index, point ->
                val markerColor = ComposeColor(colors.getOrElse(index) { colors.firstOrNull() ?: 0xFFD6E9FFL }.toInt())
                Surface(
                    modifier = Modifier
                        .offset {
                            val radiusPx = with(density) { 18.dp.roundToPx() }
                            IntOffset(
                                (point.x * previewSize.width).roundToInt() - radiusPx,
                                (point.y * previewSize.height).roundToInt() - radiusPx
                            )
                        }
                        .size(36.dp)
                        .pointerInput(index, previewSize) {
                            detectDragGestures(
                                onDragStart = { selectedPoint = index },
                                onDrag = { _, dragAmount ->
                                    if (previewSize.width > 0 && previewSize.height > 0) {
                                        selectedPoint = index
                                        val current = samplePoints[index]
                                        updatePoint(
                                            index,
                                            Offset(
                                                current.x + dragAmount.x / previewSize.width,
                                                current.y + dragAmount.y / previewSize.height
                                            )
                                        )
                                    }
                                }
                            )
                        },
                    shape = RoundedCornerShape(50),
                    color = markerColor,
                    border = BorderStroke(
                        if (selectedPoint == index) 3.dp else 2.dp,
                        ComposeColor.White.copy(alpha = 0.94f)
                    ),
                    onClick = { selectedPoint = index }
                ) {}
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickSheetLiquidAction(
            label = "取消",
            enabled = true,
            backdrop = backdrop,
            config = config,
            modifier = Modifier.weight(1f),
            height = SleepDownDesignTokens.CenteredDialog.ActionHeight,
            onClick = onCancel
        )
        QuickSheetLiquidAction(
            label = "确认",
            enabled = bitmap != null,
            backdrop = backdrop,
            config = config,
            modifier = Modifier.weight(1f),
            primary = true,
            height = SleepDownDesignTokens.CenteredDialog.ActionHeight,
            onClick = onConfirm
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalizePanel(
    modifier: Modifier = Modifier,
    drawSurface: Boolean = true,
    state: AppState,
    backdrop: Backdrop?,
    mode: HomeMode,
    weekCardHeightScale: Float,
    weekCardHeightScaleFloor: Float,
    onWeekCardHeightScale: (Float) -> Unit,
    onWeekCardHeightScalePreview: (Float) -> Unit,
    onPickWallpaper: () -> Unit,
    onUpdateConfig: (String, ScheduleConfigEntity) -> Unit,
    onPreviewConfig: (String, ScheduleConfigEntity) -> Unit,
    previewSliderKey: String?,
    previewProgress: Float,
    onSliderPreviewActiveChange: (String, Boolean) -> Unit
) {
    val inheritedCoursePalette = LocalCourseCardPalette.current
    var showCourseColorDialog by remember { mutableStateOf(false) }
    var courseColorDialogMode by remember { mutableStateOf(CourseCardColorMode.SOLID) }
    var courseColorDialogStage by remember { mutableStateOf(CourseColorDialogStage.Picker) }
    var courseColorEditorColors by remember { mutableStateOf(listOf(0xFFD6E9FFL)) }
    var courseColorEditorIndex by remember { mutableIntStateOf(0) }

    fun openCourseColorDialog(colorMode: CourseCardColorMode) {
        val seed = state.config.cardColorArgb
            .takeUnless { it == MulticolorCourseCardArgb }
            ?: 0xFFD6E9FFL
        val initial = when (colorMode) {
            CourseCardColorMode.SOLID,
            CourseCardColorMode.GRADIENT -> listOf(seed)
            CourseCardColorMode.COLORFUL -> {
                val configured = decodeCourseCardPalette(state.config.courseCardPalette)
                    .ifEmpty { inheritedCoursePalette }
                    .ifEmpty { DefaultCourseCardPalette }
                    .take(4)
                buildList {
                    addAll(configured)
                    DefaultCourseCardPalette.forEach { fallback ->
                        if (size < 4 && fallback !in this) add(fallback)
                    }
                }.take(4)
            }
        }
        courseColorDialogMode = colorMode
        courseColorDialogStage = CourseColorDialogStage.Picker
        courseColorEditorColors = initial
        courseColorEditorIndex = 0
        showCourseColorDialog = true
    }

    fun updateCourseColorEditor(index: Int, color: Long) {
        val next = courseColorEditorColors.toMutableList()
        while (next.size <= index) {
            next += DefaultCourseCardPalette[next.size % DefaultCourseCardPalette.size]
        }
        next[index] = color and 0xFFFFFFFFL
        courseColorEditorColors = next
    }

    fun commitCourseColorEditor() {
        val primary = courseColorEditorColors.firstOrNull() ?: 0xFFD6E9FFL
        val candidate = when (courseColorDialogMode) {
            CourseCardColorMode.SOLID,
            CourseCardColorMode.GRADIENT -> state.config.copy(
                cardColorArgb = primary,
                courseCardColorMode = courseColorDialogMode
            )
            CourseCardColorMode.COLORFUL -> state.config.copy(
                cardColorArgb = primary,
                courseCardColorMode = CourseCardColorMode.COLORFUL,
                courseCardPalette = encodeCourseCardPalette(courseColorEditorColors)
            )
        }
        onUpdateConfig(PersonalizeCardColorChange, candidate)
        showCourseColorDialog = false
    }

    var activeTouchSliderKey by remember { mutableStateOf<String?>(null) }
    fun updateSliderTouchOwner(key: String, active: Boolean) {
        activeTouchSliderKey = resolveActivePersonalizationSlider(
            currentKey = activeTouchSliderKey,
            eventKey = key,
            active = active
        )
    }
    @Composable
    fun PersonalizeSection(content: @Composable ColumnScope.() -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    ComposeColor.Black.copy(
                        alpha = 0.10f * (1f - previewProgress.coerceIn(0f, 1f))
                    )
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content
        )
    }
    @Composable
    fun PanelContent(modifier: Modifier = Modifier) {
        Column(modifier = modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PersonalizeSection {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .personalizePreviewVisibility(previewSliderKey, previewProgress),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("首页壁纸", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        LiquidMenuButton(
                            backdrop,
                            "\u9009\u62E9\u58C1\u7EB8",
                            onClick = onPickWallpaper,
                            textColorOverride = ComposeColor.White,
                            surfaceColorOverride = ComposeColor(0xFF0A84FF).copy(alpha = 0.72f)
                        )
                        if (state.config.wallpaperUri != null) {
                            LiquidMenuButton(
                                backdrop,
                                "\u6E05\u9664",
                                onClick = {
                                    onUpdateConfig(
                                        PersonalizeWallpaperContentChange,
                                        state.config.copy(
                                            wallpaperUri = null,
                                            wallpaperPortraitCenterX = 0.5f,
                                            wallpaperPortraitCenterY = 0.5f,
                                            wallpaperPortraitScale = 1f,
                                            wallpaperLandscapeCenterX = 0.5f,
                                            wallpaperLandscapeCenterY = 0.5f,
                                            wallpaperLandscapeScale = 1f,
                                            wallpaperSourceWidth = null,
                                            wallpaperSourceHeight = null,
                                            homeTextLight = false
                                        )
                                    )
                                },
                                textColorOverride = ComposeColor.White,
                                surfaceColorOverride = ComposeColor(0xFFFF453A).copy(alpha = 0.66f)
                            )
                        }
                    }
                }
                WallpaperBlurControl(
                    blurDp = state.config.wallpaperBlur,
                    backdrop = backdrop,
                    onCommit = { percent ->
                        onUpdateConfig(
                            PersonalizeWallpaperBlurSlider,
                            state.config.copy(wallpaperBlur = wallpaperBlurDp(percent))
                        )
                    },
                    onPreviewValueChange = { percent ->
                        onPreviewConfig(
                            PersonalizeWallpaperBlurSlider,
                            state.config.copy(wallpaperBlur = wallpaperBlurDp(percent))
                        )
                    },
                    previewSliderKey = previewSliderKey,
                    previewProgress = previewProgress,
                    onSliderPreviewActiveChange = onSliderPreviewActiveChange,
                    onTouchActiveChange = {
                        updateSliderTouchOwner(PersonalizeWallpaperBlurSlider, it)
                    }
                )
                PersonalizeValueSlider(
                    sliderKey = PersonalizeWallpaperBrightnessSlider,
                    value = state.config.wallpaperBrightness.coerceIn(0.35f, 1f),
                    valueRange = 0.35f..1f,
                    backdrop = backdrop,
                    label = { "壁纸亮度 ${(it * 100).toInt()}%" },
                    onCommit = {
                        onUpdateConfig(
                            PersonalizeWallpaperBrightnessSlider,
                            state.config.copy(wallpaperBrightness = it)
                        )
                    },
                    onPreviewValueChange = {
                        onPreviewConfig(
                            PersonalizeWallpaperBrightnessSlider,
                            state.config.copy(wallpaperBrightness = it)
                        )
                    },
                    previewSliderKey = previewSliderKey,
                    previewProgress = previewProgress,
                    onSliderPreviewActiveChange = onSliderPreviewActiveChange,
                    snapValue = 1f,
                    onTouchActiveChange = {
                        updateSliderTouchOwner(PersonalizeWallpaperBrightnessSlider, it)
                    }
                )
            }
            PersonalizeSection {
                if (mode == HomeMode.Week) {
                    Text(
                        text = "周视图行高",
                        modifier = Modifier
                            .fillMaxWidth()
                            .personalizePreviewVisibility(previewSliderKey, previewProgress),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Column(
                        modifier = Modifier.personalizePreviewVisibility(
                            previewSliderKey,
                            previewProgress,
                            PersonalizeWeekHeightSlider
                        ),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        Box {
                            LiquidControlSlider(
                                value = weekCardHeightSliderFromScale(
                                    scale = weekCardHeightScale,
                                    minimumScale = weekCardHeightScaleFloor
                                ),
                                onValueChange = {
                                    onWeekCardHeightScale(
                                        weekCardHeightScaleFromSlider(
                                            progress = it,
                                            minimumScale = weekCardHeightScaleFloor
                                        )
                                    )
                                },
                                valueRange = 0f..1f,
                                backdrop = backdrop,
                                onLiveValueChange = {
                                    onWeekCardHeightScalePreview(
                                        weekCardHeightScaleFromSlider(
                                            progress = it,
                                            minimumScale = weekCardHeightScaleFloor
                                        )
                                    )
                                },
                                snapValue = 0.5f,
                                onSliderTouchActiveChange = { active ->
                                    updateSliderTouchOwner(PersonalizeWeekHeightSlider, active)
                                    onSliderPreviewActiveChange(PersonalizeWeekHeightSlider, active)
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-2).dp)
                        ) {
                            Text(
                                "紧凑",
                                modifier = Modifier.align(Alignment.CenterStart),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                "自适应",
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                "宽松",
                                modifier = Modifier.align(Alignment.CenterEnd),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .personalizePreviewVisibility(previewSliderKey, previewProgress),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("课程卡片颜色", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(8.dp))
                CourseColorModeRow(
                    title = "纯色",
                    mode = CourseCardColorMode.SOLID,
                    selectedMode = state.config.courseCardColorMode,
                    presets = SolidCourseColorPresets.map { listOf(it) },
                    selectedPreset = { it.firstOrNull() == state.config.cardColorArgb },
                    onPresetSelected = { colors ->
                        onUpdateConfig(
                            PersonalizeCardColorChange,
                            state.config.copy(
                                cardColorArgb = colors.first(),
                                courseCardColorMode = CourseCardColorMode.SOLID
                            )
                        )
                    },
                    onOpenPalette = { openCourseColorDialog(CourseCardColorMode.SOLID) }
                )
                CourseColorModeRow(
                    title = "渐变",
                    mode = CourseCardColorMode.GRADIENT,
                    selectedMode = state.config.courseCardColorMode,
                    presets = GradientCourseColorPresets.map { listOf(it) },
                    selectedPreset = { it.firstOrNull() == state.config.cardColorArgb },
                    onPresetSelected = { colors ->
                        onUpdateConfig(
                            PersonalizeCardColorChange,
                            state.config.copy(
                                cardColorArgb = colors.first(),
                                courseCardColorMode = CourseCardColorMode.GRADIENT
                            )
                        )
                    },
                    onOpenPalette = { openCourseColorDialog(CourseCardColorMode.GRADIENT) }
                )
                CourseColorModeRow(
                    title = "彩色",
                    mode = CourseCardColorMode.COLORFUL,
                    selectedMode = state.config.courseCardColorMode,
                    presets = listOf(AutomaticColorfulCoursePreview),
                    selectedPreset = { state.config.courseCardPalette.isBlank() },
                    onPresetSelected = {
                        onUpdateConfig(
                            PersonalizeCardColorChange,
                            state.config.copy(
                                courseCardColorMode = CourseCardColorMode.COLORFUL,
                                courseCardPalette = ""
                            )
                        )
                    },
                    onOpenPalette = { openCourseColorDialog(CourseCardColorMode.COLORFUL) }
                )
                val alphaLabel = if (state.config.courseCardGlassEnabled) "课程卡片着色强度" else "课程卡片不透明度"
                PersonalizeValueSlider(
                    sliderKey = PersonalizeCardAlphaSlider,
                    value = state.config.cardAlpha.coerceIn(0f, 1f),
                    valueRange = 0f..1f,
                    backdrop = backdrop,
                    label = { "$alphaLabel ${(it * 100).toInt()}%" },
                    onCommit = {
                        onUpdateConfig(
                            PersonalizeCardAlphaSlider,
                            state.config.copy(cardAlpha = it)
                        )
                    },
                    onPreviewValueChange = {
                        onPreviewConfig(
                            PersonalizeCardAlphaSlider,
                            state.config.copy(cardAlpha = it)
                        )
                    },
                    previewSliderKey = previewSliderKey,
                    previewProgress = previewProgress,
                    onSliderPreviewActiveChange = onSliderPreviewActiveChange,
                    snapValue = 0.5f,
                    onTouchActiveChange = {
                        updateSliderTouchOwner(PersonalizeCardAlphaSlider, it)
                    }
                )
                val maxCourseCardBlur = courseCardBlurMaximum(state.config.courseCardGlassEnabled)
                PersonalizeValueSlider(
                    sliderKey = PersonalizeCardBlurSlider,
                    value = state.config.courseCardBlur.coerceIn(0f, maxCourseCardBlur) /
                        maxCourseCardBlur * 100f,
                    valueRange = 0f..100f,
                    backdrop = backdrop,
                    label = { "课程卡片模糊 ${it.toInt()}%" },
                    onCommit = {
                        onUpdateConfig(
                            PersonalizeCardBlurSlider,
                            state.config.copy(courseCardBlur = it / 100f * maxCourseCardBlur)
                        )
                    },
                    onPreviewValueChange = {
                        onPreviewConfig(
                            PersonalizeCardBlurSlider,
                            state.config.copy(courseCardBlur = it / 100f * maxCourseCardBlur)
                        )
                    },
                    previewSliderKey = previewSliderKey,
                    previewProgress = previewProgress,
                    onSliderPreviewActiveChange = onSliderPreviewActiveChange,
                    snapValue = 35f,
                    onTouchActiveChange = {
                        updateSliderTouchOwner(PersonalizeCardBlurSlider, it)
                    }
                )
                PersonalizeValueSlider(
                    sliderKey = PersonalizeCardFontSlider,
                    value = state.config.courseCardFontScale,
                    valueRange = 0.80f..1.35f,
                    backdrop = backdrop,
                    label = { "课程卡片字体 ${(it * 100).toInt()}%" },
                    onCommit = {
                        onUpdateConfig(
                            PersonalizeCardFontSlider,
                            state.config.copy(courseCardFontScale = it)
                        )
                    },
                    onPreviewValueChange = {
                        onPreviewConfig(
                            PersonalizeCardFontSlider,
                            state.config.copy(courseCardFontScale = it)
                        )
                    },
                    previewSliderKey = previewSliderKey,
                    previewProgress = previewProgress,
                    onSliderPreviewActiveChange = onSliderPreviewActiveChange,
                    snapValue = 1f,
                    onTouchActiveChange = {
                        updateSliderTouchOwner(PersonalizeCardFontSlider, it)
                    }
                )
                if (mode == HomeMode.Week) {
                    PersonalizeValueSlider(
                        sliderKey = PersonalizeWeekCornerSlider,
                        value = state.config.weekCardCornerProgress.coerceIn(0f, 1f),
                        valueRange = 0f..1f,
                        backdrop = backdrop,
                        label = { "课程卡片圆角" },
                        onCommit = {
                            onUpdateConfig(
                                PersonalizeWeekCornerSlider,
                                state.config.copy(weekCardCornerProgress = it)
                            )
                        },
                        onPreviewValueChange = {
                            onPreviewConfig(
                                PersonalizeWeekCornerSlider,
                                state.config.copy(weekCardCornerProgress = it)
                            )
                        },
                        previewSliderKey = previewSliderKey,
                        previewProgress = previewProgress,
                        onSliderPreviewActiveChange = onSliderPreviewActiveChange,
                        snapValue = 0.5f,
                        onTouchActiveChange = {
                            updateSliderTouchOwner(PersonalizeWeekCornerSlider, it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .personalizePreviewVisibility(previewSliderKey, previewProgress),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("课程卡片液态玻璃", style = MaterialTheme.typography.labelLarge)
                    LiquidControlToggle(
                        checked = state.config.courseCardGlassEnabled,
                        onCheckedChange = {
                            onUpdateConfig(
                                PersonalizeCardGlassChange,
                                state.config.copy(courseCardGlassEnabled = it)
                            )
                        },
                        backdrop = backdrop
                    )
                }
            }
        }
    }
    val scrollState = rememberScrollState()
    val contentModifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState, enabled = activeTouchSliderKey == null)
    val panelTextColor = personalizePanelForegroundColor(state.config)
    if (!drawSurface) {
        Box(modifier = modifier) {
            CompositionLocalProvider(LocalContentColor provides panelTextColor) {
                PanelContent(contentModifier)
            }
        }
    } else if (backdrop != null) {
        val lightGlass = glassUsesLightStyle(state.config)
        LiquidPanel(
            backdrop = backdrop,
            modifier = modifier,
            surfaceColor = if (lightGlass) ComposeColor.White.copy(alpha = 0.18f) else ComposeColor(0xFF121212).copy(alpha = 0.30f)
        ) {
            CompositionLocalProvider(LocalContentColor provides panelTextColor) {
                PanelContent(contentModifier)
            }
        }
    } else {
        GlassDialogSurface(
            backdrop = null,
            config = state.config,
            modifier = modifier
        ) {
            CompositionLocalProvider(LocalContentColor provides panelTextColor) {
                PanelContent(contentModifier)
            }
        }
    }
    CourseColorEditorDialog(
        show = showCourseColorDialog,
        stage = courseColorDialogStage,
        mode = courseColorDialogMode,
        colors = courseColorEditorColors,
        selectedIndex = courseColorEditorIndex,
        backdrop = backdrop,
        config = state.config,
        onDismiss = { showCourseColorDialog = false },
        onStageChange = { courseColorDialogStage = it },
        onSelectedIndexChange = { courseColorEditorIndex = it },
        onColorChange = ::updateCourseColorEditor,
        onConfirm = ::commitCourseColorEditor
    )
}

@Composable
private fun personalizePanelForegroundColor(config: ScheduleConfigEntity): ComposeColor {
    return homeForegroundColor(config)
}

@Composable
fun LiquidMenuButton(
    backdrop: Backdrop?,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
    textColorOverride: ComposeColor? = null,
    surfaceColorOverride: ComposeColor? = null
) {
    val textColor = textColorOverride ?: if (destructive) ComposeColor(0xFFFF453A) else MaterialTheme.colorScheme.primary
    val surfaceColor = surfaceColorOverride ?: if (destructive) ComposeColor(0xFFFF453A).copy(alpha = 0.16f) else ComposeColor.White.copy(alpha = 0.10f)
    if (backdrop != null) {
        LiquidButton(
            onClick = onClick,
            backdrop = backdrop,
            modifier = modifier,
            height = 40.dp,
            surfaceColor = surfaceColor,
            contentPadding = PaddingValues(horizontal = 14.dp),
            blurRadius = 8.dp,
            lensHeight = 24.dp,
            lensAmount = 28.dp,
            chromaticAberration = false
        ) {
            Text(label, color = textColor, style = MaterialTheme.typography.labelMedium, maxLines = 1, softWrap = false)
        }
    } else {
        Text(
            label,
            modifier = modifier
                .clip(RoundedCornerShape(50))
                .background(surfaceColor.copy(alpha = surfaceColor.alpha.coerceAtLeast(0.14f)))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            softWrap = false
        )
    }
}

open class SettingsDetailActivityHost : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // The coordinator selects a translucent destination for snapshot-free anchored routes
        // before super.onCreate. The QuickSheet route also declares this policy in the manifest so
        // its captured underlay can be installed without an opaque preview frame.
        ActivityTransitionCoordinator.prepareDestinationBeforeOnCreate(this)
        super.onCreate(savedInstanceState)
        ActivityTransitionCoordinator.installDestinationWindowBackground(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val externalBackupPreviewSource = intent.data?.takeIf {
            intent.action == Intent.ACTION_VIEW
        }
        val customizeScheduleId = intent.getIntExtra(ScheduleCustomizeIdExtra, -1).takeIf { it > 0 }
        val useEntrySnapshot = intent.getBooleanExtra(ScheduleEntrySnapshotExtra, false)
        setContent {
            val app = application as CourseScheduleApp
            val viewModel: ScheduleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = ScheduleViewModelFactory(app, app.repository)
            )
            val section = if (externalBackupPreviewSource != null) {
                SettingsPage.BackupPreview
            } else {
                SettingsPage.valueOf(
                    intent.getStringExtra(SettingsDetailPageExtra) ?: SettingsPage.General.name
                )
            }
            val backupPreviewSource = externalBackupPreviewSource
                ?: intent.getStringExtra(BackupPreviewUriExtra)?.toUri()
            val stateFlow = if (customizeScheduleId != null || section == SettingsPage.ScheduleManager) {
                viewModel.allSchedulesState
            } else {
                viewModel.state
            }
            val state by stateFlow.collectAsStateWithLifecycle()
            // Anchored entries keep the destination window transparent during the Morph. The
            // special QuickSheet route supplies its root-composited underlay to the shared
            // snapshot blur chain instead of relying on OEM cross-window blur.
            val isAnchoredSettingsEntry = remember {
                intent.transitionRouteIdOrNull()
                    ?.let(TransitionRouteCatalog::get)
                    ?.legacyProfile is LegacyTransitionProfile.Anchored
            }
            val isQuickSheetSettingsEntry = remember {
                intent.transitionRouteIdOrNull() == TransitionRouteId.QuickSheetToSettingsDetail
            }
            CourseScheduleTheme(config = state.config) {
                val darkWindowBackground = appUsesDarkTheme(state.config)
                LaunchedEffect(darkWindowBackground, isAnchoredSettingsEntry) {
                    // Keep the Activity surface consistent with the Compose page while it is
                    // leaving the screen. Some Android 13/ColorOS back animations otherwise
                    // reveal the light theme's static window background for one frame.
                    if (!isAnchoredSettingsEntry) {
                        window.applyAppThemeSurface(darkWindowBackground)
                    }
                }
                val scheduleEditState = remember(state, customizeScheduleId) {
                    if (customizeScheduleId != null) scheduleConfigStateForEdit(state, customizeScheduleId) else state
                }
                val scheduleEditReady = customizeScheduleId == null ||
                    (
                        state.allConfigs.any { it.id == customizeScheduleId } &&
                            state.allPeriods.any { it.scheduleId == customizeScheduleId }
                    )
                val editEntrySnapshot = remember(customizeScheduleId, useEntrySnapshot) {
                    customizeScheduleId
                        ?.takeIf { useEntrySnapshot }
                        ?.let { scheduleId ->
                            BitmapFactory.decodeFile(
                                ScheduleSnapshotStore.file(this, scheduleId).absolutePath
                            )
                        }
                }
                var editEntrySnapshotVisible by remember(editEntrySnapshot) {
                    mutableStateOf(editEntrySnapshot != null)
                }
                LaunchedEffect(editEntrySnapshot) {
                    if (editEntrySnapshot != null) {
                        withFrameNanos { }
                        withFrameNanos { }
                        editEntrySnapshotVisible = false
                    }
                }
                var scheduleExitRequest by remember { mutableIntStateOf(0) }
                var widgetEditorVisible by remember { mutableStateOf(false) }
                var interceptSystemBack by remember(section) { mutableStateOf(false) }
                // Anchored entries share the same destination-side Morph host; ordinary settings
                // entries retain their platform/depth transition behavior.
                var transitionRequestClose by remember { mutableStateOf<(() -> Unit)?>(null) }
                val closeSettings: () -> Unit = { (transitionRequestClose ?: { finish() })() }
                val requestExit: () -> Unit = {
                    when (section) {
                        SettingsPage.Schedule -> scheduleExitRequest++
                        else -> closeSettings()
                    }
                }
                // An Activity-level callback disables Android's cross-Activity predictive-back
                // animation. Only install it while a page really has a pending draft that must be
                // committed or confirmed; clean pages stay on the native predictive-back path.
                BackHandler(enabled = interceptSystemBack, onBack = requestExit)
                Box(Modifier.fillMaxSize()) {
                val settingsDetailContent: @Composable () -> Unit = {
                DetailActivityScaffold(
                    title = section.title(),
                    config = state.config,
                    compactTopBar = section.usesPersistentCenteredSettingsTitle(),
                    centerCompactTitle = section.usesPersistentCenteredSettingsTitle(),
                    compactTitleMatchesSettings = section.usesPersistentCenteredSettingsTitle(),
                    topBarVisible = !widgetEditorVisible,
                    onBack = requestExit
                ) { backdrop ->
                    when (section) {
                        SettingsPage.General -> GeneralSettingsScreen(
                            state = state,
                            backdrop = backdrop,
                            onUpdateConfig = viewModel::saveGeneralSettings,
                            onOpenLiquidGlass = {
                                ActivityTransitionCoordinator.openImmediate(
                                    this@SettingsDetailActivityHost,
                                    TransitionRouteId.SettingsToSettingsDetail,
                                    Intent(this@SettingsDetailActivityHost, SettingsDetailActivity::class.java)
                                        .putExtra(SettingsDetailPageExtra, SettingsPage.LiquidGlass.name)
                                )
                            }
                        )
                        SettingsPage.LiquidGlass -> LiquidGlassSettingsScreen(
                            state = state,
                            backdrop = backdrop,
                            onUpdateBlurScale = viewModel::saveHomeChromeBlurScale
                        )
                        SettingsPage.Widgets -> WidgetCustomizationScreen(
                            state = state,
                            backdrop = backdrop,
                            onEditorVisibilityChange = { widgetEditorVisible = it }
                        )
                        SettingsPage.DayAgent -> DayAgentSettingsScreen(state, backdrop)
                        SettingsPage.Schedule -> if (!scheduleEditReady) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                androidx.compose.material3.CircularProgressIndicator()
                            }
                        } else {
                            ScheduleConfigScreen(
                                scheduleEditState,
                                backdrop,
                                SettingsSection.Schedule,
                                onSave = { config, periods ->
                                    val targetId = customizeScheduleId
                                    if (targetId != null) {
                                        viewModel.saveConfigForSchedule(targetId, config, periods)
                                    } else {
                                        viewModel.saveConfig(config, periods)
                                    }
                                },
                                exitCommitRequest = scheduleExitRequest,
                                onExitCommitFinished = { saved -> if (saved) closeSettings() },
                                onExitInterceptionChange = { interceptSystemBack = it }
                            )
                        }
                        SettingsPage.Notifications -> ScheduleConfigScreen(
                            state = state,
                            backdrop = backdrop,
                            section = SettingsSection.Notifications,
                            onSave = { config, _ -> viewModel.saveNotificationSettings(config) },
                        )
                        SettingsPage.About -> AboutSettingsScreen(
                            state = state,
                            backdrop = backdrop,
                            onPageChange = { target ->
                                ActivityTransitionCoordinator.openImmediate(
                                    this@SettingsDetailActivityHost,
                                    TransitionRouteId.SettingsToSettingsDetail,
                                    Intent(this@SettingsDetailActivityHost, SettingsDetailActivity::class.java)
                                        .putExtra(SettingsDetailPageExtra, target.name)
                                )
                            }
                        )
                        SettingsPage.BackupRestore -> BackupRestoreSettingsScreen(
                            state = state,
                            backdrop = backdrop,
                            onOpenPreview = { source ->
                                ActivityTransitionCoordinator.openImmediate(
                                    this@SettingsDetailActivityHost,
                                    TransitionRouteId.SettingsToSettingsDetail,
                                    Intent(this@SettingsDetailActivityHost, SettingsDetailActivity::class.java)
                                        .setData(source)
                                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        .putExtra(SettingsDetailPageExtra, SettingsPage.BackupPreview.name)
                                        .putExtra(BackupPreviewUriExtra, source.toString())
                                )
                            }
                        )
                        SettingsPage.BackupPreview -> if (backupPreviewSource != null) {
                            BackupRestorePreviewScreen(state, backdrop, backupPreviewSource)
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "没有找到要预览的备份文件，请返回后重新选择。",
                                    modifier = Modifier.padding(24.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        SettingsPage.Changelog -> AboutSettingsScreen(
                            state = state,
                            backdrop = backdrop,
                            onPageChange = { target ->
                                ActivityTransitionCoordinator.openImmediate(
                                    this@SettingsDetailActivityHost,
                                    TransitionRouteId.SettingsToSettingsDetail,
                                    Intent(this@SettingsDetailActivityHost, SettingsDetailActivity::class.java)
                                        .putExtra(SettingsDetailPageExtra, target.name)
                                )
                            }
                        )
						SettingsPage.PrivacyPolicy -> PrivacyPolicySettingsScreen(state, backdrop)
                        SettingsPage.ScheduleManager -> ScheduleManagerScreen(
                            state = state, backdrop = backdrop,
                            onCreateSchedule = { viewModel.createSchedule(it) },
                            onActivateSchedule = { id, finish -> viewModel.activateSchedule(id, finish) },
                            onRenameSchedule = { id, name -> viewModel.renameSchedule(id, name) },
                            onDeleteSchedule = { viewModel.deleteSchedule(it) }
                        )
                        SettingsPage.Root -> SettingsRootScreen(state, backdrop) {}
                    }
                }
                }
                if (isAnchoredSettingsEntry) {
                    CrossActivityTransitionHost(
                        activity = this@SettingsDetailActivityHost,
                        // A new Activity-local ViewModel starts from AppState(). Starting the
                        // Morph against that placeholder consumes the whole opening duration
                        // before the selected schedule arrives, which looks like a skipped
                        // transition. Precompose the real page, then release the existing motion.
                        openingReady = !isQuickSheetSettingsEntry || scheduleEditReady,
                        sourceContent = {
                            // The QuickSheet route already supplies the exact composed button and
                            // full background frame. Painting a synthetic source card here would
                            // stretch a flat color across the expanding shell, so leave this layer
                            // transparent; other anchored settings entries retain their fallback.
                            if (!isQuickSheetSettingsEntry) {
                                val sourceDark = appUsesDarkTheme(state.config)
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (sourceDark) {
                                                ComposeColor(0xFF25272D)
                                            } else {
                                                ComposeColor(0xFFE9ECF2)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "详细设置",
                                        color = if (sourceDark) ComposeColor.White else ComposeColor(0xFF111111),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    ) { requestClose ->
                        if (transitionRequestClose == null) transitionRequestClose = requestClose
                        settingsDetailContent()
                    }
                } else {
                    settingsDetailContent()
                }
                AnimatedVisibility(
                    visible = editEntrySnapshotVisible,
                    enter = EnterTransition.None,
                    exit = fadeOut(animationSpec = tween(220)),
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(200f)
                ) {
                    editEntrySnapshot?.let { snapshot ->
                        Image(
                            bitmap = snapshot.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                }
            }
        }
    }
}

private fun scheduleConfigStateForEdit(state: AppState, scheduleId: Int): AppState {
    val targetConfig = state.allConfigs.firstOrNull { it.id == scheduleId }
        ?: state.config.takeIf { it.id == scheduleId }
        ?: defaultConfig(scheduleId)
    val targetPeriods = state.allPeriods.filter { it.scheduleId == scheduleId }
        .ifEmpty { state.periods.takeIf { targetConfig.id == state.config.id } ?: defaultPeriods(scheduleId) }
    return state.copy(config = targetConfig.copy(id = scheduleId), periods = targetPeriods)
}

@Composable
fun LiquidControlSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    backdrop: Backdrop?,
    modifier: Modifier = Modifier,
    onLiveValueChange: ((Float) -> Unit)? = null,
    snapValue: Float? = null,
    onSliderTouchActiveChange: (Boolean) -> Unit = {},
    visibilityThreshold: Float = 0.01f,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val previewDispatchScope = rememberCoroutineScope()
    val latestLiveValueChange by rememberUpdatedState(onLiveValueChange)
    var pendingLiveValue by remember { mutableStateOf<Float?>(null) }
    var previewDispatchJob by remember { mutableStateOf<Job?>(null) }
    val safeSnapValue = snapValue?.coerceIn(valueRange)
    var localValue by remember(valueRange) { mutableFloatStateOf(value.coerceIn(valueRange)) }
    var previewModeActive by remember(valueRange) { mutableStateOf(false) }
    var lastSnapSide by remember(valueRange, safeSnapValue) {
        mutableIntStateOf(snapSide(value.coerceIn(valueRange), safeSnapValue, visibilityThreshold))
    }
    var snapZoneActive by remember(valueRange, safeSnapValue) {
        mutableStateOf(lastSnapSide == 0 && safeSnapValue != null)
    }

    fun updatePreview(candidate: Float) {
        val next = candidate.coerceIn(valueRange)
        val nextSide = snapSide(next, safeSnapValue, visibilityThreshold)
        val enteredSnapZone = safeSnapValue != null && nextSide == 0 && !snapZoneActive
        val crossedSnap = nextSide != 0 && !snapZoneActive &&
            lastSnapSide != 0 && nextSide != lastSnapSide
        if (enteredSnapZone || crossedSnap) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        snapZoneActive = safeSnapValue != null && nextSide == 0
        if (nextSide != 0) lastSnapSide = nextSide
        localValue = next
        pendingLiveValue = next
        if (previewDispatchJob?.isActive != true) {
            previewDispatchJob = previewDispatchScope.launch {
                withFrameNanos { }
                val latest = pendingLiveValue
                pendingLiveValue = null
                previewDispatchJob = null
                if (latest != null) latestLiveValueChange?.invoke(latest)
            }
        }
    }

    fun commit(candidate: Float) {
        val finalValue = candidate.coerceIn(valueRange)
        localValue = finalValue
        previewDispatchJob?.cancel()
        previewDispatchJob = null
        pendingLiveValue = null
        // The exact release value is never delayed behind frame conflation.
        latestLiveValueChange?.invoke(finalValue)
        onValueChange(finalValue)
    }

    LaunchedEffect(value, valueRange) {
        val externalValue = value.coerceIn(valueRange)
        if (!previewModeActive && abs(localValue - externalValue) > visibilityThreshold) {
            localValue = externalValue
            lastSnapSide = snapSide(externalValue, safeSnapValue, visibilityThreshold)
            snapZoneActive = safeSnapValue != null && lastSnapSide == 0
        }
    }

    SliderWithSnapMarker(
        modifier = modifier,
        snapValue = safeSnapValue,
        valueRange = valueRange
    ) {
        if (backdrop != null && enabled) {
            LiquidSlider(
                value = { localValue },
                onPreviewValueChange = ::updatePreview,
                onPreviewModeChange = { active ->
                    previewModeActive = active
                    onSliderTouchActiveChange(active)
                },
                onCommit = ::commit,
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                backdrop = backdrop,
                snapValue = safeSnapValue,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Slider(
                value = localValue,
                enabled = enabled,
                onValueChange = {
                    if (!previewModeActive) {
                        previewModeActive = true
                        onSliderTouchActiveChange(true)
                    }
                    updatePreview(it)
                },
                onValueChangeFinished = {
                    commit(localValue)
                    if (previewModeActive) {
                        previewModeActive = false
                        onSliderTouchActiveChange(false)
                    }
                },
                valueRange = valueRange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun snapSide(candidate: Float, snapValue: Float?, threshold: Float): Int {
    val snap = snapValue ?: return 0
    return when {
        candidate < snap - threshold -> -1
        candidate > snap + threshold -> 1
        else -> 0
    }
}

@Composable
private fun SliderWithSnapMarker(
    modifier: Modifier,
    snapValue: Float?,
    valueRange: ClosedFloatingPointRange<Float>,
    content: @Composable () -> Unit
) {
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        content()
        val snap = snapValue ?: return@BoxWithConstraints
        val fraction = ((snap - valueRange.start) /
            (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        Canvas(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(36.dp)
        ) {
            val radius = 2.5.dp.toPx()
            val visualFraction = if (isLtr) fraction else 1f - fraction
            val centerX = radius +
                (size.width - radius * 2f).coerceAtLeast(0f) * visualFraction
            drawCircle(
                color = ComposeColor.White.copy(alpha = 0.78f),
                radius = radius,
                center = Offset(centerX, size.height / 2f)
            )
        }
    }
}
@Composable
fun LiquidControlToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    backdrop: Backdrop?,
    modifier: Modifier = Modifier
) {
    if (backdrop != null) {
        LiquidToggle(
            selected = { checked },
            onSelect = onCheckedChange,
            backdrop = backdrop,
            modifier = modifier
        )
    } else {
        Switch(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier)
    }
}

@Composable
fun DockItem(selected: Boolean, backdrop: Backdrop?, config: ScheduleConfigEntity, iconRes: Int, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .height(60.dp)
            .width(78.dp),
        contentAlignment = Alignment.Center
    ) {
        if (pressed) {
            GlassLens(
                backdrop = backdrop,
                config = config,
                pressProgress = 1f,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier = Modifier.padding(vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
        ) {
            Icon(painterResource(iconRes), contentDescription = null, modifier = Modifier.size(22.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
fun SettingsScreen(
    page: SettingsPage,
    state: AppState,
    backdrop: Backdrop?,
    onPageChange: (SettingsPage) -> Unit,
    onSave: (ScheduleConfigEntity, List<PeriodEntity>) -> Unit,
    onUpdateConfig: (ScheduleConfigEntity) -> Unit,
    onUpdateGeneralConfig: (ScheduleConfigEntity) -> Unit = onUpdateConfig,
    onUpdateHomeChromeBlurScale: (Float) -> Unit,
    onCreateSchedule: (String) -> Unit = {},
    onActivateSchedule: (Int, (() -> Unit)?) -> Unit = { _, _ -> },
    onRenameSchedule: (Int, String) -> Unit = { _, _ -> },
    onDeleteSchedule: (Int) -> Unit = {},
    exitCommitRequest: Int = 0,
    onExitCommitFinished: (Boolean) -> Unit = {},
    onExitInterceptionChange: (Boolean) -> Unit = {}
) {
    val pageConfig = settingsVisualConfig(state.config)
    val pageState = state.copy(config = pageConfig)
    val adaptiveMetrics = rememberHomeAdaptiveMetrics()
    var backupPreviewUriString by rememberSaveable { mutableStateOf<String?>(null) }
    GlassMiuixSettingsTheme(pageConfig) {
        if (page == SettingsPage.Root && adaptiveMetrics.isLargeScreen) {
            var tabletNavigation by rememberSaveable(
                stateSaver = TabletSettingsNavigationStateSaver
            ) {
                mutableStateOf(TabletSettingsNavigationState())
            }
            var detailNavigationDirection by remember { mutableIntStateOf(0) }
            var tabletWidgetEditorVisible by remember { mutableStateOf(false) }
            var pendingPageName by remember { mutableStateOf<String?>(null) }
            var scheduleExitInFlight by remember { mutableStateOf(false) }
            var scheduleExitRequest by remember { mutableIntStateOf(0) }
            var externalExitInFlight by remember { mutableStateOf(false) }
            var lastHandledExternalExitRequest by remember { mutableIntStateOf(exitCommitRequest) }
            var exitInterceptionByPage by remember {
                mutableStateOf<Map<SettingsPage, Boolean>>(emptyMap())
            }
            val selectedPage = tabletNavigation.rootPage
            val displayedPage = tabletNavigation.displayedPage
            val portrait = adaptiveMetrics.screenHeight > adaptiveMetrics.screenWidth
            val navigationWidth = if (portrait) {
                (adaptiveMetrics.screenWidth * 0.38f).coerceIn(280.dp, 328.dp)
            } else {
                (adaptiveMetrics.screenWidth * 0.31f).coerceIn(336.dp, 408.dp)
            }
            LaunchedEffect(displayedPage) {
                if (displayedPage != SettingsPage.Widgets) tabletWidgetEditorVisible = false
            }
            val interceptRootExit = tabletNavigation.detailPages.isEmpty() &&
                exitInterceptionByPage[selectedPage] == true
            LaunchedEffect(interceptRootExit) {
                onExitInterceptionChange(interceptRootExit)
            }
            DisposableEffect(onExitInterceptionChange) {
                onDispose { onExitInterceptionChange(false) }
            }
            fun applyTabletRootPage(nextPage: SettingsPage) {
                detailNavigationDirection = 0
                tabletNavigation = tabletNavigation.selectRoot(nextPage)
                backupPreviewUriString = null
                pendingPageName = null
            }
            fun requestTabletRootPage(nextPage: SettingsPage) {
                if (
                    tabletNavigation.detailPages.isEmpty() &&
                    exitInterceptionByPage[selectedPage] == true
                ) {
                    pendingPageName = nextPage.name
                    if (!scheduleExitInFlight) {
                        scheduleExitInFlight = true
                        scheduleExitRequest++
                    }
                } else {
                    applyTabletRootPage(nextPage)
                }
            }
            fun pushTabletDetailPage(nextPage: SettingsPage) {
                val nextNavigation = tabletNavigation.pushDetail(nextPage)
                if (nextNavigation == tabletNavigation) return
                detailNavigationDirection = 1
                tabletNavigation = nextNavigation
            }
            fun popTabletDetailPage() {
                val nextNavigation = tabletNavigation.popDetail()
                if (nextNavigation == tabletNavigation) return
                detailNavigationDirection = -1
                tabletNavigation = nextNavigation
            }
            LaunchedEffect(exitCommitRequest) {
                if (exitCommitRequest == lastHandledExternalExitRequest) return@LaunchedEffect
                lastHandledExternalExitRequest = exitCommitRequest
                if (
                    tabletNavigation.detailPages.isEmpty() &&
                    exitInterceptionByPage[selectedPage] == true
                ) {
                    externalExitInFlight = true
                    if (!scheduleExitInFlight) {
                        scheduleExitInFlight = true
                        scheduleExitRequest++
                    }
                } else {
                    onExitCommitFinished(true)
                }
            }
            BackHandler(enabled = tabletNavigation.detailPages.isNotEmpty()) {
                popTabletDetailPage()
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(settingsPageBackground(pageConfig))
            ) {
                Box(Modifier.width(navigationWidth).fillMaxHeight()) {
                    SettingsRootScreen(
                        state = pageState,
                        backdrop = backdrop,
                        selectedPage = selectedPage,
                        onPageChange = ::requestTabletRootPage
                    )
                }
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.46f))
                )
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clipToBounds()
                ) {
                    AnimatedContent(
                        targetState = displayedPage,
                        transitionSpec = {
                            when {
                                detailNavigationDirection > 0 -> {
                                    slideInHorizontally(
                                        tween(500, easing = MiuixSettingsNavigationMotion)
                                    ) { width -> width } togetherWith
                                        slideOutHorizontally(
                                            tween(500, easing = MiuixSettingsNavigationMotion)
                                        ) { width -> -width / 4 }
                                }
                                detailNavigationDirection < 0 -> {
                                    slideInHorizontally(
                                        tween(500, easing = MiuixSettingsNavigationMotion)
                                    ) { width -> -width / 4 } togetherWith
                                        slideOutHorizontally(
                                            tween(500, easing = MiuixSettingsNavigationMotion)
                                        ) { width -> width }
                                }
                                else -> EnterTransition.None togetherWith ExitTransition.None
                            }
                        },
                        label = "TabletSettingsDetailNavigation"
                    ) { targetPage ->
                        GlassMiuixTabletDetailPaneScaffold(
                            title = targetPage.title(),
                            config = pageConfig,
                            topBarVisible = !(targetPage == SettingsPage.Widgets && tabletWidgetEditorVisible),
                            showBackButton = targetPage != selectedPage,
                            useMiuixCollapsedTitleStyle =
                                targetPage.usesPersistentCenteredSettingsTitle(),
                            onBack = ::popTabletDetailPage,
                            modifier = Modifier.fillMaxSize()
                        ) { paneBackdrop ->
                            SettingsPageContent(
                                page = targetPage,
                                state = state,
                                pageState = pageState,
                                backdrop = paneBackdrop,
                                onPageChange = ::pushTabletDetailPage,
                                onSave = onSave,
                                onUpdateConfig = onUpdateConfig,
                                onUpdateGeneralConfig = onUpdateGeneralConfig,
                                onUpdateHomeChromeBlurScale = onUpdateHomeChromeBlurScale,
                                onCreateSchedule = onCreateSchedule,
                                onActivateSchedule = onActivateSchedule,
                                onRenameSchedule = onRenameSchedule,
                                onDeleteSchedule = onDeleteSchedule,
                                onWidgetEditorVisibilityChange = { tabletWidgetEditorVisible = it },
                                backupPreviewUri = backupPreviewUriString?.toUri(),
                                onOpenBackupPreview = { source ->
                                    backupPreviewUriString = source.toString()
                                    pushTabletDetailPage(SettingsPage.BackupPreview)
                                },
                                exitCommitRequest = scheduleExitRequest,
                                onExitCommitFinished = { completed ->
                                    scheduleExitInFlight = false
                                    scheduleExitRequest = 0
                                    if (externalExitInFlight) {
                                        externalExitInFlight = false
                                        pendingPageName = null
                                        onExitCommitFinished(completed)
                                    } else {
                                        val destination = pendingPageName
                                        pendingPageName = null
                                        if (completed && destination != null) {
                                            applyTabletRootPage(
                                                runCatching { SettingsPage.valueOf(destination) }
                                                    .getOrDefault(SettingsPage.General)
                                            )
                                        }
                                    }
                                },
                                onExitInterceptionChange = { needsInterception ->
                                    exitInterceptionByPage = exitInterceptionByPage.toMutableMap().apply {
                                        if (needsInterception) put(targetPage, true) else remove(targetPage)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        } else {
            SettingsPageContent(
                page = page,
                state = state,
                pageState = pageState,
                backdrop = backdrop,
                onPageChange = onPageChange,
                onSave = onSave,
                onUpdateConfig = onUpdateConfig,
                onUpdateGeneralConfig = onUpdateGeneralConfig,
                onUpdateHomeChromeBlurScale = onUpdateHomeChromeBlurScale,
                onCreateSchedule = onCreateSchedule,
                onActivateSchedule = onActivateSchedule,
                onRenameSchedule = onRenameSchedule,
                onDeleteSchedule = onDeleteSchedule,
                backupPreviewUri = backupPreviewUriString?.toUri(),
                onOpenBackupPreview = { source ->
                    backupPreviewUriString = source.toString()
                    onPageChange(SettingsPage.BackupPreview)
                },
                exitCommitRequest = exitCommitRequest,
                onExitCommitFinished = onExitCommitFinished,
                onExitInterceptionChange = onExitInterceptionChange
            )
        }
    }
}

@Composable
private fun SettingsPageContent(
    page: SettingsPage,
    state: AppState,
    pageState: AppState,
    backdrop: Backdrop?,
    onPageChange: (SettingsPage) -> Unit,
    onSave: (ScheduleConfigEntity, List<PeriodEntity>) -> Unit,
    onUpdateConfig: (ScheduleConfigEntity) -> Unit,
    onUpdateGeneralConfig: (ScheduleConfigEntity) -> Unit,
    onUpdateHomeChromeBlurScale: (Float) -> Unit,
    onCreateSchedule: (String) -> Unit,
    onActivateSchedule: (Int, (() -> Unit)?) -> Unit,
    onRenameSchedule: (Int, String) -> Unit,
    onDeleteSchedule: (Int) -> Unit,
    onWidgetEditorVisibilityChange: (Boolean) -> Unit = {},
    backupPreviewUri: Uri? = null,
    onOpenBackupPreview: (Uri) -> Unit = {},
    exitCommitRequest: Int = 0,
    onExitCommitFinished: (Boolean) -> Unit = {},
    onExitInterceptionChange: (Boolean) -> Unit = {}
) {
    when (page) {
        SettingsPage.Root -> SettingsRootScreen(pageState, backdrop, onPageChange = onPageChange)
        SettingsPage.General -> GeneralSettingsScreen(
            state = state,
            backdrop = backdrop,
            onUpdateConfig = onUpdateGeneralConfig,
            onOpenLiquidGlass = { onPageChange(SettingsPage.LiquidGlass) },
            exitCommitRequest = exitCommitRequest,
            onExitCommitFinished = onExitCommitFinished
        )
        SettingsPage.LiquidGlass -> LiquidGlassSettingsScreen(
            state = state,
            backdrop = backdrop,
            onUpdateBlurScale = onUpdateHomeChromeBlurScale
        )
        SettingsPage.Widgets -> WidgetCustomizationScreen(
            state,
            backdrop,
            onEditorVisibilityChange = onWidgetEditorVisibilityChange
        )
        SettingsPage.DayAgent -> DayAgentSettingsScreen(state, backdrop)
        SettingsPage.Schedule -> ScheduleConfigScreen(
            state = state,
            backdrop = backdrop,
            section = SettingsSection.Schedule,
            onSave = onSave,
            exitCommitRequest = exitCommitRequest,
            onExitCommitFinished = onExitCommitFinished,
            onExitInterceptionChange = onExitInterceptionChange
        )
        SettingsPage.Notifications -> ScheduleConfigScreen(
            state = state,
            backdrop = backdrop,
            section = SettingsSection.Notifications,
            onSave = { config, _ -> onUpdateConfig(config) },
            exitCommitRequest = exitCommitRequest,
            onExitCommitFinished = onExitCommitFinished
        )
        SettingsPage.ScheduleManager -> ScheduleManagerScreen(
            state,
            backdrop,
            onCreateSchedule,
            onActivateSchedule,
            onRenameSchedule,
            onDeleteSchedule
        )
        SettingsPage.BackupRestore -> BackupRestoreSettingsScreen(
            state = pageState,
            backdrop = backdrop,
            onOpenPreview = onOpenBackupPreview
        )
        SettingsPage.BackupPreview -> if (backupPreviewUri != null) {
            BackupRestorePreviewScreen(pageState, backdrop, backupPreviewUri)
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "没有找到要预览的备份文件，请返回后重新选择。",
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        SettingsPage.About -> AboutSettingsScreen(pageState, backdrop, onPageChange = onPageChange)
        SettingsPage.Changelog -> AboutSettingsScreen(pageState, backdrop, onPageChange = onPageChange)
		SettingsPage.PrivacyPolicy -> PrivacyPolicySettingsScreen(pageState, backdrop)
    }
}

@Composable
fun SettingsRootScreen(
    state: AppState,
    backdrop: Backdrop?,
    selectedPage: SettingsPage? = null,
    onPageChange: (SettingsPage) -> Unit
) {
    val context = LocalContext.current
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        }.getOrDefault("1.0")
    }
    val appName = remember {
        runCatching {
            context.packageManager.getApplicationLabel(context.applicationInfo).toString()
        }.getOrDefault("LX课程表")
    }

    GlassMiuixRootSettingsScaffold(
        title = "设置",
        config = state.config
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = DockScrollPadding
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        item {
            SettingsGroup(backdrop = backdrop, config = state.config, modifier = Modifier.fillMaxWidth()) {
                top.yukonga.miuix.kmp.preference.ArrowPreference(
                    title = appName,
                    summary = "Liao 开发",
                    startAction = {
                        Image(
                            painter = painterResource(R.mipmap.ic_launcher),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    },
                    endActions = {
                        Text(
                            "版本 $versionName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(
                                alpha = if (selectedPage == SettingsPage.About) 0.10f else 0f
                            )
                        ),
                    insideMargin = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    onClick = { onPageChange(SettingsPage.About) }
                )
            }
        }
        item {
            GlassPreferenceSection("应用") {
                SettingsGroup(backdrop = backdrop, config = state.config, modifier = Modifier.fillMaxWidth()) {
                    SettingsNavigationRow(
                        "通用设置",
                        "深色模式与系统外观",
                        selected = selectedPage == SettingsPage.General,
                        onClick = { onPageChange(SettingsPage.General) }
                    )
                    SettingsDivider()
                    SettingsNavigationRow(
                        "小组件设置",
                        "自定义小组件背景",
                        selected = selectedPage == SettingsPage.Widgets,
                        onClick = { onPageChange(SettingsPage.Widgets) }
                    )
                    SettingsDivider()
                    SettingsNavigationRow(
                        "当前课表详细设置",
                        "编辑当前课表的周数、节次与显示规则",
                        selected = selectedPage == SettingsPage.Schedule,
                        onClick = { onPageChange(SettingsPage.Schedule) }
                    )
                    SettingsDivider()
                    SettingsNavigationRow(
                        "通知设置",
                        "上课提醒",
                        selected = selectedPage == SettingsPage.Notifications,
                        onClick = { onPageChange(SettingsPage.Notifications) }
                    )
                }
            }
        }
        item {
            GlassPreferenceSection("今日助手") {
                SettingsGroup(backdrop = backdrop, config = state.config, modifier = Modifier.fillMaxWidth()) {
                    SettingsNavigationRow(
                        "今日助手",
                        "管理日视图助手、天气与预警。",
                        selected = selectedPage == SettingsPage.DayAgent,
                        onClick = { onPageChange(SettingsPage.DayAgent) }
                    )
                }
            }
        }
        item {
            GlassPreferenceSection("其他") {
                SettingsGroup(backdrop = backdrop, config = state.config, modifier = Modifier.fillMaxWidth()) {
                    SettingsNavigationRow(
                        "备份与恢复",
                        "保存课表和设置，或从备份恢复",
                        selected = selectedPage == SettingsPage.BackupRestore,
                        onClick = { onPageChange(SettingsPage.BackupRestore) }
                    )
                }
            }
        }
        }
    }
}

@Composable
fun ScheduleManagerScreen(
    state: AppState,
    backdrop: Backdrop?,
    onCreateSchedule: (String) -> Unit,
    onActivateSchedule: (Int, (() -> Unit)?) -> Unit,
    onRenameSchedule: (Int, String) -> Unit,
    onDeleteSchedule: (Int) -> Unit
) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var deleteTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }
    val topPadding = detailContentTopPadding()
    val coursesPerSchedule = remember(state.allCourses) {
        state.allCourses.groupBy { it.scheduleId }.mapValues { it.value.size }
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = topPadding, bottom = DockScrollPadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "课表设置",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                SettingsActionButton("新建课表", backdrop, onClick = { showCreateDialog = true })
            }
        }
        item {
            SettingsGroup(backdrop = backdrop, config = state.config, modifier = Modifier.fillMaxWidth()) {
                state.schedules.forEachIndexed { idx, profile ->
                    val courseCount = coursesPerSchedule[profile.id] ?: 0
                    val isActive = profile.isActive
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                context.openRegisteredActivity(
                                    TransitionRouteId.SettingsToSettingsDetail,
                                    Intent(context, SettingsDetailActivity::class.java)
                                        .putExtra(SettingsDetailPageExtra, SettingsPage.Schedule.name)
                                        .putExtra(ScheduleCustomizeIdExtra, profile.id)
                                )
                            }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    profile.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isActive) {
                                    Text(
                                        "当前",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                "$courseCount 门课程",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (!isActive) {
                                SettingsActionButton("设为", backdrop, onClick = {
                                    onActivateSchedule(profile.id, null)
                                })
                            }
                            SettingsActionButton("重命名", backdrop, onClick = {
                                renameTarget = Pair(profile.id, profile.name)
                            })
                            if (!isActive && state.schedules.size > 1) {
                                SettingsActionButton("删除", backdrop, onClick = {
                                    deleteTarget = Pair(profile.id, profile.name)
                                }, destructive = true)
                            }
                        }
                    }
                    if (idx != state.schedules.lastIndex) SettingsDivider()
                }
            }
        }
    }

    if (showCreateDialog) {
        ScheduleNameDialog(
            title = "新建课表",
            initialName = "",
            backdrop = backdrop,
            config = state.config,
            onConfirm = { name ->
                showCreateDialog = false
                if (name.isNotBlank()) onCreateSchedule(name)
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    renameTarget?.let { (id, name) ->
        ScheduleNameDialog(
            title = "重命名课表",
            initialName = name,
            backdrop = backdrop,


            config = state.config,
            onConfirm = { newName ->
                renameTarget = null
                if (newName.isNotBlank()) onRenameSchedule(id, newName)
            },
            onDismiss = { renameTarget = null }
        )
    }

    deleteTarget?.let { (id, name) ->
        LiquidAlertDialog(
            title = "删除课表",
            message = "确定要删除「$name」吗？该课表下的所有课程都会被删除，且无法恢复。",
            actions = listOf(
                LiquidAlertAction("取消", LiquidAlertActionStyle.Secondary) { deleteTarget = null },
                LiquidAlertAction("确认删除", LiquidAlertActionStyle.Destructive) {
                    val targetId = id
                    deleteTarget = null
                    onDeleteSchedule(targetId)
                }
            ),
            backdrop = backdrop,
            config = state.config,
            onDismissRequest = { deleteTarget = null }
        )
    }
}

@Composable
fun ScheduleNameDialog(
    title: String,
    initialName: String,
    backdrop: Backdrop?,
    config: ScheduleConfigEntity,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    val textColor = glassForegroundColor(config)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        CenterLiquidDialog(backdrop = backdrop, config = config) {
            LiquidDialogHeader(title, onDismiss, backdrop, config, onConfirm = { onConfirm(name.trim()) })
            DialogCapsuleField(name, { name = it }, "课表名称", config, Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun AboutSettingsScreen(
    state: AppState,
    backdrop: Backdrop?,
    onPageChange: (SettingsPage) -> Unit
) {
    val context = LocalContext.current
    val topPadding = detailContentTopPadding()
    val appName = remember {
        runCatching {
            context.packageManager.getApplicationLabel(context.applicationInfo).toString()
        }.getOrDefault("LX 课程表")
    }
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        }.getOrDefault("1.0")
    }
    SleepDownSecondaryPageList(
        contentTopPadding = topPadding,
        contentBottomPadding = DockScrollPadding
    ) {
        item(key = "about-app-name") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 72.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher),
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "版本 $versionName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item(key = "about-privacy") {
            SettingsGroup(
                backdrop = backdrop,
                config = state.config,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp)
            ) {
                SettingsNavigationRow(
                    title = "隐私政策",
                    subtitle = "查看 LX 课程表隐私政策",
                    onClick = { onPageChange(SettingsPage.PrivacyPolicy) }
                )
            }
        }
    }
}


private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
