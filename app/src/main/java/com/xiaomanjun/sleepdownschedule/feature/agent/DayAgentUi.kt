package com.xiaomanjun.sleepdownschedule.feature.agent

import com.xiaomanjun.sleepdownschedule.core.ui.designsystem.*
import com.xiaomanjun.sleepdownschedule.glass.ui.*
import com.xiaomanjun.sleepdownschedule.*

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyant.backdrop.Backdrop
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.LinkedHashMap

/**
 * The day page is disposed while the week page is entering. Keep the expensive, immutable facts
 * projection outside that short-lived composition so switching back does not scan and sort the
 * whole timetable inside the transition frame budget again.
 */
private object DayAgentRenderCache {
    private data class FactsKey(
        val scheduleId: Int,
        val date: LocalDate,
        val coursesHash: Int,
        val periodsHash: Int,
        val configHash: Int,
        val scheduleName: String?,
        val weatherHash: Int
    )

    private val facts = object : LinkedHashMap<FactsKey, DayAgentFacts>(6, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<FactsKey, DayAgentFacts>?): Boolean =
            size > 6
    }

    @Synchronized
    fun facts(
        state: AppState,
        date: LocalDate,
        weather: AgentWeatherSnapshot?,
        scheduleName: String?
    ): DayAgentFacts {
        val key = FactsKey(
            scheduleId = state.config.id,
            date = date,
            coursesHash = state.courses.hashCode(),
            periodsHash = state.periods.hashCode(),
            configHash = state.config.hashCode(),
            scheduleName = scheduleName,
            weatherHash = weather.hashCode()
        )
        return facts.getOrPut(key) {
            buildDayAgentFacts(
                state.courses,
                state.periods,
                state.config,
                date,
                weather,
                scheduleName
            )
        }
    }
}

private data class DayAgentCardVisual(
    val activityLabel: String,
    val courseName: String?,
    val countdownText: String,
    val locationText: String,
    val focusTimeText: String,
    val courseCountText: String,
    val weatherText: String,
    val trailingStatus: String?,
    val weatherAlert: Boolean,
    val collapsed: Boolean,
    val cardIsDark: Boolean
)

@Composable
fun TodayAgentHost(
    state: AppState,
    date: LocalDate,
    backdrop: Backdrop?,
    textColor: Color,
    collapsed: Boolean,
    isActive: Boolean = true
) {
    val context = LocalContext.current
    var enabled by remember(date, state.config.id) { mutableStateOf(DayAgentPreferences.isEnabled(context)) }
    val preferenceVersion by DayAgentPreferences.changes.collectAsStateWithLifecycle(initialValue = 0L)

    LaunchedEffect(preferenceVersion) {
        enabled = DayAgentPreferences.isEnabled(context)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        enabled = DayAgentPreferences.isEnabled(context)
    }

    // 首次启用弹窗已移除：默认即启用，可在「今日助手」设置中随时关闭。
    if (enabled) {
        TodayAgentCard(
            state = state,
            date = date,
            backdrop = backdrop,
            collapsed = collapsed,
            isActive = isActive,
            weatherEnabled = DayAgentPreferences.isWeatherEnabled(context)
        )
    }
}

@Composable
fun TodayAgentCard(
    state: AppState,
    date: LocalDate,
    backdrop: Backdrop?,
    collapsed: Boolean,
    isActive: Boolean,
    weatherEnabled: Boolean
) {
    val context = LocalContext.current
    val scheduleId = state.config.id
    val scheduleName = state.schedules.firstOrNull { it.id == scheduleId }?.name
    val weatherRepository = remember(context) { DayAgentWeatherRepository(context.applicationContext) }
    val scope = rememberCoroutineScope()
    var weather by remember(date, weatherEnabled) {
        mutableStateOf(if (weatherEnabled) DayAgentWeatherStore.load(context) else null)
    }
    var now by remember(date) { mutableStateOf(LocalDateTime.now()) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            scope.launch { weather = weatherRepository.getWeather(forceRefresh = true) }
        }
    }

    LaunchedEffect(date) {
        while (true) {
            now = LocalDateTime.now()
            delay(60_000L - (System.currentTimeMillis() % 60_000L))
        }
    }

    val staticFacts = remember(state.courses, state.periods, state.config, scheduleName, date, weather) {
        DayAgentRenderCache.facts(state, date, weather, scheduleName)
    }
    val facts = remember(staticFacts, now) {
        staticFacts.copy(now = now)
    }
    LaunchedEffect(scheduleId, date, weatherEnabled, isActive) {
        // Weather is the only asynchronous home-card content; only the settled page fetches it.
        delay(280)
        if (isActive && weatherEnabled && weather == null) weather = weatherRepository.getWeather()
    }
    val foreground = LocalAdaptiveGlass.current.contentColor
    val cardIsDark = !glassUsesLightStyle(state.config)
    val activityAccent = if (cardIsDark) Color(0xFF62B5FF) else Color(0xFF006EDC)
    val currentSlot = remember(facts.today, now) {
        facts.today.firstOrNull { !now.toLocalTime().isBefore(it.start) && now.toLocalTime().isBefore(it.end) }
    }
    val nextSlot = remember(facts.today, now) {
        facts.today.firstOrNull { now.toLocalTime().isBefore(it.start) }
    }
    val previewTomorrow = now.toLocalDate() == date &&
        now.toLocalTime() >= LocalTime.of(22, 0) &&
        currentSlot == null &&
        nextSlot == null &&
        facts.tomorrow.isNotEmpty()
    val focusSlot = currentSlot ?: nextSlot ?: facts.tomorrow.firstOrNull().takeIf { previewTomorrow }
    val remainingMinutes = remember(currentSlot, nextSlot, now) {
        val target = currentSlot?.end ?: nextSlot?.start
        target?.let { Duration.between(now.toLocalTime(), it).toMinutes().coerceAtLeast(0) }
    }
    val activityLabel = when {
        currentSlot != null -> "当前"
        nextSlot != null -> "下节课"
        previewTomorrow -> "明日首课"
        facts.today.isEmpty() -> "今日无课"
        else -> "课程已结束"
    }
    val countdownText = when {
        currentSlot != null && remainingMinutes != null -> "${remainingMinutes} 分钟后下课"
        nextSlot != null && remainingMinutes != null && now.toLocalTime() >= LocalTime.of(6, 0) ->
            "${remainingMinutes} 分钟后"
        previewTomorrow -> ""
        facts.today.isEmpty() -> "轻松一天"
        else -> "今日完成"
    }
    val locationText = focusSlot?.course?.let { course ->
        listOfNotNull(course.location?.takeIf(String::isNotBlank), course.teacher?.takeIf(String::isNotBlank)).joinToString(" | ")
    }.orEmpty()
    val focusTimeText = focusSlot?.let {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        "${it.start.format(formatter)} - ${it.end.format(formatter)}"
    }.orEmpty()
    val weatherText = when {
        !weatherEnabled -> "天气未启用"
        weather != null -> {
            val icon = weatherEmoji(weather!!.summary)
            val condition = weather!!.summary.substringBefore('，')
            "$icon ${weather!!.temperature}°C $condition"
        }
        !weatherRepository.hasLocationPermission() -> "📍 点击开启天气"
        else -> "天气加载中"
    }
    val weatherAlertText = weather?.let(::weatherAlertText)
    val cardVisual = DayAgentCardVisual(
        activityLabel = activityLabel,
        courseName = focusSlot?.course?.name,
        countdownText = countdownText,
        locationText = locationText,
        focusTimeText = focusTimeText,
        courseCountText = if (previewTomorrow) {
            "明天有 ${facts.tomorrow.size} 节课"
        } else {
            "今天有 ${facts.today.size} 节课"
        },
        weatherText = weatherText,
        trailingStatus = weatherAlertText?.let { "⚠️ $it" },
        weatherAlert = weatherAlertText != null,
        collapsed = collapsed,
        cardIsDark = cardIsDark
    )
    val cardShape = RoundedRectangle(if (collapsed) 26.dp else 28.dp)
    val cardTokens = GlassTokens.dialog(intensity = 1.12f).copy(
        blur = if (cardIsDark) 10.dp else 12.dp,
        lensHeight = 18.dp,
        lensAmount = 34.dp,
        surfaceAlpha = if (cardIsDark) 0.54f else 0.58f,
        borderAlpha = 0.34f,
        highlightAlpha = 0.075f,
        depthEffect = true,
        chromaticAberration = false
    )
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // This is the one real glass shell. It is a sibling of the interactive content, so its
        // compact card-sized texture can safely feed the LiquidButtons without recursive capture.
        GlassSurface(
            backdrop = backdrop,
            config = state.config,
            modifier = Modifier.matchParentSize(),
            shape = cardShape,
            tokens = cardTokens
        ) {}
        DayAgentCardVisualContent(
            visual = cardVisual,
            foreground = foreground,
            activityAccent = activityAccent,
            modifier = Modifier.fillMaxWidth(),
            onWeatherClick = if (
                weatherEnabled &&
                weather == null &&
                !weatherRepository.hasLocationPermission()
            ) {
                { permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
            } else null
        )
    }
}

private fun weatherEmoji(summary: String): String {
    // The suffix may contain precipitation advice/probability even when the current condition is
    // merely overcast. Only classify the actual condition segment before the first separator.
    val condition = summary.substringBefore('，').substringBefore(',').trim()
    return when {
    "雷" in condition -> "⛈️"
    "雪" in condition -> "🌨️"
    "雨" in condition -> "🌧️"
    "雾" in condition || "霾" in condition -> "🌫️"
    "阴" in condition -> "☁️"
    "云" in condition -> "⛅"
    "晴" in condition -> "☀️"
    else -> "🌤️"
    }
}

private fun weatherAlertText(weather: AgentWeatherSnapshot): String? {
    val condition = weather.summary.substringBefore('，').substringBefore(',').trim()
    val alerts = buildList {
        when {
            "雷" in condition -> add("雷暴提醒")
            "雪" in condition -> add("降雪提醒")
            "暴雨" in condition -> add("强降雨提醒")
            weather.precipitationProbability >= 60 -> add("降雨提醒")
        }
        if (weather.temperature >= 35 || weather.apparentTemperature >= 38) add("高温提醒")
        if (weather.temperature <= 5 || weather.apparentTemperature <= 2) add("低温提醒")
        if (weather.windSpeed >= 35) add("大风提醒")
    }
    return alerts.distinct().takeIf { it.isNotEmpty() }?.joinToString(" · ")
}

@Composable
private fun DayAgentCardVisualContent(
    visual: DayAgentCardVisual,
    foreground: Color,
    activityAccent: Color,
    modifier: Modifier = Modifier,
    onWeatherClick: (() -> Unit)? = null
) {
    val shape = RoundedRectangle(if (visual.collapsed) 26.dp else 28.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                if (visual.cardIsDark) {
                    Color.Black.copy(alpha = 0.20f)
                } else {
                    Color.White.copy(alpha = 0.30f)
                }
            )
            .padding(horizontal = 16.dp, vertical = if (visual.collapsed) 10.dp else 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    visual.activityLabel,
                    color = activityAccent,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                visual.courseName?.let { courseName ->
                    Spacer(Modifier.width(8.dp))
                    Text(
                        courseName,
                        modifier = Modifier.weight(1f),
                        color = foreground,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } ?: Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                if (visual.countdownText.isNotBlank()) {
                    Text(
                        visual.countdownText,
                        color = activityAccent,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
            if (visual.courseName != null) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        visual.locationText,
                        modifier = Modifier.weight(1f),
                        color = foreground.copy(alpha = 0.56f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        visual.focusTimeText,
                        color = foreground.copy(alpha = 0.56f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }
        if (!visual.collapsed) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(foreground.copy(alpha = 0.10f)))
            Text(
                visual.courseCountText,
                color = foreground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    visual.weatherText,
                    modifier = Modifier
                        .weight(1f)
                        .then(if (onWeatherClick != null) Modifier.clickable(onClick = onWeatherClick) else Modifier),
                    color = foreground.copy(alpha = 0.58f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                visual.trailingStatus?.let { status ->
                    Spacer(Modifier.width(6.dp))
                     Text(
                         text = status,
                         color = if (visual.weatherAlert) {
                            if (visual.cardIsDark) Color(0xFFFFB86B) else Color(0xFFB84D00)
                        } else {
                            foreground.copy(alpha = 0.52f)
                        },
                        style = if (visual.weatherAlert) {
                            MaterialTheme.typography.labelMedium
                        } else {
                            MaterialTheme.typography.labelSmall
                        },
                        fontWeight = if (visual.weatherAlert) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
