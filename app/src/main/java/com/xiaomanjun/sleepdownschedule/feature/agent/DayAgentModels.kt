package com.xiaomanjun.sleepdownschedule.feature.agent


import com.xiaomanjun.sleepdownschedule.*

import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class AgentWeatherSnapshot(
    val summary: String,
    val temperature: Int,
    val apparentTemperature: Int,
    val precipitationProbability: Int,
    val windSpeed: Int,
    val fetchedAt: Long
) {
    val hasAlert: Boolean
        get() = precipitationProbability >= 60 || temperature >= 35 || temperature <= 5 || windSpeed >= 35
}

data class AgentCourseSlot(
    val course: CourseEntity,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime
)

data class AgentPersistedMessageContent(
    val text: String,
    val attachmentFileName: String? = null
)

private val AgentImageMarkerRegex =
    Regex("""^\[\[agent_image:([A-Za-z0-9._-]+)]]\r?\n?""")

fun agentMessageContent(text: String, attachmentFileName: String?): String {
    return if (attachmentFileName.isNullOrBlank()) {
        text
    } else {
        "[[agent_image:$attachmentFileName]]\n$text"
    }
}

fun parseAgentMessageContent(content: String): AgentPersistedMessageContent {
    val match = AgentImageMarkerRegex.find(content)
    return AgentPersistedMessageContent(
        text = if (match == null) content else content.removeRange(match.range),
        attachmentFileName = match?.groupValues?.getOrNull(1)
    )
}

data class DayAgentFacts(
    val date: LocalDate,
    val now: LocalDateTime,
    val today: List<AgentCourseSlot>,
    val tomorrow: List<AgentCourseSlot>,
    val week: List<AgentCourseSlot>,
    val weather: AgentWeatherSnapshot?,
    val sourceHash: String,
    val periodDefinitions: List<PeriodEntity> = emptyList(),
    val totalWeeks: Int = 20,
    val scheduleId: Int = 1,
    val currentWeek: Int = 1,
    val termState: ScheduleTermState = ScheduleTermState.MANUAL,
    val termStatus: String = "手动设置 · 第 1 周"
)

fun buildDayAgentFacts(
    courses: List<CourseEntity>,
    periods: List<PeriodEntity>,
    config: ScheduleConfigEntity,
    date: LocalDate,
    weather: AgentWeatherSnapshot?,
    scheduleName: String? = null,
    now: LocalDateTime = LocalDateTime.now()
): DayAgentFacts {
    val scheduleCourses = courses.filter { it.scheduleId == config.id }
    val schedulePeriods = periods.filter { it.scheduleId == config.id }
    val termState = derivedScheduleTermState(config, date)
    val termStatus = scheduleTermStatusDescription(config, date)
    val currentWeek = effectiveCurrentWeek(config, date)
    fun slotsFor(targetDate: LocalDate): List<AgentCourseSlot> {
        val week = scheduleWeekForDateOrNull(config, targetDate) ?: return emptyList()
        val weekday = targetDate.dayOfWeek.value
        return scheduleCourses.asSequence()
            .filter { it.weekday == weekday && week in it.weeks && parityMatches(it.weekParity, week) }
            .mapNotNull { course ->
                val start = courseStartTime(course, schedulePeriods)
                val end = courseEndTime(course, schedulePeriods)
                if (start == null || end == null) null else AgentCourseSlot(course, targetDate, start, end)
            }
            .sortedBy { it.start }
            .toList()
    }

    val today = slotsFor(date)
    val tomorrow = slotsFor(date.plusDays(1))
    val weekStart = date.minusDays((date.dayOfWeek.value - 1).toLong())
    val week = (0L..6L).flatMap { offset -> slotsFor(weekStart.plusDays(offset)) }
    val source = buildString {
        append(config.id).append('|').append(date).append('|').append(termState).append(':')
            .append(currentWeek).append('|')
        week.forEach { slot ->
            append(slot.course.id).append(':').append(slot.course.name).append(':')
            append(slot.start).append('-').append(slot.end).append(':')
            append(slot.course.location.orEmpty()).append('|')
        }
    }
    return DayAgentFacts(
        date = date,
        now = now,
        today = today,
        tomorrow = tomorrow,
        week = week,
        weather = weather,
        sourceHash = source.sha256(),
        periodDefinitions = schedulePeriods.sortedBy { it.periodIndex },
        totalWeeks = config.totalWeeks,
        scheduleId = config.id,
        currentWeek = currentWeek,
        termState = termState,
        termStatus = termStatus
    )
}

private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
    .digest(toByteArray(Charsets.UTF_8))
    .joinToString("") { "%02x".format(it) }
