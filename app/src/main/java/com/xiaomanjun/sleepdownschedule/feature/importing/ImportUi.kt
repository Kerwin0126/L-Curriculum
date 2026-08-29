package com.xiaomanjun.sleepdownschedule.feature.importing

import com.xiaomanjun.sleepdownschedule.app.ui.*
import com.xiaomanjun.sleepdownschedule.core.ui.designsystem.*
import com.xiaomanjun.sleepdownschedule.glass.ui.*
import com.xiaomanjun.sleepdownschedule.feature.home.*
import com.xiaomanjun.sleepdownschedule.feature.home.day.*
import com.xiaomanjun.sleepdownschedule.feature.settings.*
import com.xiaomanjun.sleepdownschedule.app.config.SleepDownRemoteConfig
import com.xiaomanjun.sleepdownschedule.core.remoteconfig.*
import com.xiaomanjun.sleepdownschedule.*


import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


/** 「复制提示词」按钮复制到剪贴板的完整口令提示词，逐字保持用户提供的原文。 */
private const val CopyAiPromptText = """请读取我发给你的课表 PDF/图片等文件，并只输出 LX 课程表口令，不要解释、不要 Markdown。
口令格式：
SDCT1
T=总周数
P=节次,开始时间-结束时间;节次,开始时间-结束时间
C=课程名|教师|地点|星期|节次|周次|单双周|备注

规则：
1. 星期用 1-7 表示周一到周日。
2. 节次用逗号或范围，例如 1,2 或 1-2。
3. 周次用逗号或范围，例如 1-16 或 1-8,10,12-16。
4. 单双周用 A/O/E，分别表示全部/单周/双周。
5. 没有教师、地点或备注时写 -。
6. 时间必须是 HH:mm。
7. 必须严格换行：SDCT1 单独一行，T= 单独一行，全部节次写在同一条 P= 行；每一门课程各占一条独立的 C= 行。
8. 不要把一条 P= 或 C= 记录折成多行，不要添加项目符号、序号、空行、Markdown 代码围栏或解释文字。
9. 课程名称、教师、地点和备注中不要使用竖线“|”；如原文包含竖线，请改为空格。每个 C= 行必须恰好包含 8 个由“|”分隔的字段。
示例：
SDCT1
T=20
P=1,08:00-08:45;2,08:55-09:40;3,10:00-10:45
C=专业硕士综合英语|嬴老师|公教楼110|1|3-5|2-13|A|-
C=矩阵论|-|cc211|3|3|2-19|O|-"""

@Composable
fun NormalizedAiManualImportScreen(
    state: AppState,
    backdrop: Backdrop?,
    onCancel: () -> Unit,
    onParsed: (ImportDraft) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var jsonText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var routeMessage by remember { mutableStateOf<String?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedMode by remember { mutableIntStateOf(0) }
    val icsFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) launcher@{ uri ->
        if (uri == null) return@launcher
        selectedFileName = null
        routeMessage = null
        error = null
        scope.launch {
            loadExternalIcsFile(context, uri)
                .onSuccess { file ->
                    selectedFileName = file.displayName
                    if (!file.isIcs) {
                        error = "所选文件不是有效的 ICS 日历文件"
                        return@onSuccess
                    }
                    routeMessage = "ICS 将在本机解析。"
                    IcsScheduleCodec.parse(file.bytes, state.config)
                        .onSuccess { draft ->
                            error = null
                            onParsed(draft)
                        }
                        .onFailure { error = it.message ?: "ICS 解析失败" }
                }
                .onFailure { error = it.message ?: "ICS 文件读取失败" }
        }
    }
    fun parseDraft() {
        val result = ScheduleImportParser.parse(jsonText, state.config)
        result.onSuccess {
            error = null
            onParsed(it)
        }.onFailure { error = it.message ?: "口令解析失败" }
    }
    fun copyAiPrompt() {
        context.getSystemService(ClipboardManager::class.java)
            .setPrimaryClip(ClipData.newPlainText("LX 课程表口令提示词", CopyAiPromptText))
        Toast.makeText(context, "已复制！请携带课表文件一并发给AI。", Toast.LENGTH_SHORT).show()
    }
    AiManualImportDialogContent(
        state = state,
        backdrop = backdrop,
        onCancel = onCancel,
        selectedMode = selectedMode,
        onModeSelected = {
            selectedMode = it
            error = null
        },
        jsonText = jsonText,
        onJsonTextChange = { jsonText = it },
        onCleanText = { jsonText = ScheduleImportParser.cleanMarkdown(jsonText) },
        onCopyPrompt = ::copyAiPrompt,
        selectedFileName = selectedFileName,
        routeMessage = routeMessage,
        error = error,
        onPrimaryAction = {
            when (selectedMode) {
                0 -> parseDraft()
                1 -> icsFileLauncher.launch(
                    arrayOf("text/calendar", "application/ics", "application/octet-stream")
                )
            }
        }
    )
}

@Composable
private fun AiManualImportDialogContent(
    state: AppState,
    backdrop: Backdrop?,
    onCancel: () -> Unit,
    selectedMode: Int,
    onModeSelected: (Int) -> Unit,
    jsonText: String,
    onJsonTextChange: (String) -> Unit,
    onCleanText: () -> Unit,
    onCopyPrompt: () -> Unit,
    selectedFileName: String?,
    routeMessage: String?,
    error: String?,
    onPrimaryAction: () -> Unit
) {
    // This destination is rendered directly on the Home glass/backdrop. Its complete foreground
    // domain must therefore follow that glass, independently from the application day/night mode.
    val textColor = glassForegroundColor(state.config)
    val dialogLightStyle = glassUsesLightStyle(state.config)
    val windowSize = currentWindowSizeDp()
    val safeInsets = WindowInsets.safeDrawing.asPaddingValues()
    val safeHeight = (
        windowSize.height -
            safeInsets.calculateTopPadding() -
            safeInsets.calculateBottomPadding() -
            32.dp
        ).coerceAtLeast(280.dp)
    // All three modes share one stable middle-sized shell. Changing import mode now swaps only
    // the inner content; the platform dialog and its expensive backdrop are never remeasured.
    val panelHeight = minOf(safeHeight, 500.dp)
    val mode = selectedMode
    CompositionLocalProvider(LocalContentColor provides textColor) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(panelHeight)
    ) {
        LiquidDialogHeader(
            title = "手动导入课表",
            onDismiss = onCancel,
            backdrop = backdrop,
            config = state.config,
            buttonBlurRadius = 4.dp,
            buttonLightStyleOverride = dialogLightStyle,
            onConfirm = onPrimaryAction
        )
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            LiquidOptionTabs(
                selectedIndex = selectedMode,
                labels = listOf("粘贴口令", "导入 ICS"),
                backdrop = backdrop,
                config = state.config,
                width = maxWidth,
                highContrast = true,
                followAppTheme = false,
                onSelected = onModeSelected
            )
        }
        if (mode == 0) {
            LiquidDialogBody {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DialogLiquidButton(
                            backdrop,
                            "清理格式",
                            onCleanText,
                            modifier = Modifier.weight(1f),
                            monochromeNeutral = true,
                            lightStyleOverride = dialogLightStyle
                        )
                        DialogLiquidButton(
                            backdrop,
                            "复制提示词",
                            onCopyPrompt,
                            modifier = Modifier.weight(1f),
                            monochromeNeutral = true,
                            lightStyleOverride = dialogLightStyle
                        )
                    }
                    DialogCapsuleField(
                        value = jsonText,
                        onValueChange = onJsonTextChange,
                        placeholder = "粘贴 AI 返回内容 或 本课表 / WakeUp / 星链 口令",
                        config = state.config,
                        minLines = 5,
                        cornerRadius = 16.dp,
                        fieldTextColor = textColor,
                        fieldLightStyleOverride = dialogLightStyle,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            LiquidDialogBody {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "选择标准 .ics 日历文件后，应用会在本机识别课程时间、重复规则和时区，并直接进入导入预览。",
                                color = textColor.copy(alpha = 0.86f),
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 21.sp
                            )
                            selectedFileName?.let { Text("已选择：$it", color = textColor) }
                            routeMessage?.let {
                                Text(it, color = textColor.copy(alpha = 0.78f), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
        error?.let {
            Text(
                it,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        LiquidDialogFooter {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DialogLiquidButton(
                    backdrop = backdrop,
                    label = when (mode) {
                        0 -> "解析并预览"
                        else -> "选择 ICS 文件"
                    },
                    role = DialogButtonRole.Confirm,
                    iconRes = R.drawable.ic_download,
                    modifier = Modifier.weight(1f),
                    onClick = onPrimaryAction
                )
            }
        }
    }
    }
}

@Composable
fun DonateSettingsScreen(
	state: AppState,
	backdrop: Backdrop?
) {
	val scope = rememberCoroutineScope()
	val remoteConfigState by SleepDownRemoteConfig.state.collectAsStateWithLifecycle()
	val donationSection = remoteConfigState.bootstrap?.donations
	LaunchedEffect(Unit) {
		SleepDownRemoteConfig.refresh(scope, force = true)
	}
    val topPadding = detailContentTopPadding()
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = topPadding, bottom = DockScrollPadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SettingsGroup(backdrop = backdrop, config = state.config, modifier = Modifier.fillMaxWidth()) {
                SettingsInfoRow("感谢支持", "LX 课程表会继续保持简洁、免费和尽量少打扰。你的捐赠会用于测试设备、应用维护和后续功能适配。捐赠完全自愿，不会影响任何功能使用。")
            }
        }
        item {
            Image(
                painter = painterResource(R.drawable.donate_reward),
                contentDescription = "微信赞赏码",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.FillWidth
            )
        }
		if (donationSection?.published == true) {
			item(key = "donation-thanks-inline") {
				DonationThanksPanel(
					state = state,
					backdrop = backdrop,
					section = donationSection
				)
			}
		}
        item {
            SettingsGroup(backdrop = backdrop, config = state.config, modifier = Modifier.fillMaxWidth()) {
                SettingsInfoRow("使用方式", "打开微信扫一扫，识别上方赞赏码即可。谢谢你愿意支持这个小小的课程表继续变好。")
            }
        }
    }
}

@Composable
fun ConfirmScheduleScreen(
    draft: ImportDraft,
    warning: String? = null,
    backdrop: Backdrop? = null,
    onCancel: () -> Unit,
    onDraftChanged: ((ImportDraft) -> Unit)? = null,
    onConfirm: (Boolean) -> Unit
) {
    val previewDraft = remember(draft) {
        draft.copy(
            periods = draft.periods.distinctBy { it.periodIndex }.sortedBy { it.periodIndex },
            courses = draft.courses.map {
                it.copy(
                    periods = it.periods.distinct().sorted(),
                    weeks = it.weeks.distinct().sorted()
                )
            }
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("即将导入 " + previewDraft.courses.size + " 门课程，请确认后写入课表。") }
        warning?.let { text ->
            item { Text(text, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
        }
        item { Text("总周数 " + previewDraft.config.totalWeeks + " 周，节次数 " + previewDraft.periods.size) }
        if (previewDraft.courses.isEmpty()) {
            item { Text("没有解析到课程", color = MaterialTheme.colorScheme.error) }
        } else {
            itemsIndexed(
                previewDraft.courses,
                key = { index, course ->
                    "preview_${index}_${course.name}_${course.weekday}_${course.periods.joinToString("_")}_${course.weeks.take(3).joinToString("_")}"
                }
            ) { _, course ->
                ImportPreviewCourseCard(course, previewDraft.periods, previewDraft.config)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("请选择导入方式：", style = MaterialTheme.typography.bodyMedium)
                LiquidAlertActions(
                    actions = listOf(
                        LiquidAlertAction("创建新课表", LiquidAlertActionStyle.Primary) { onConfirm(true) },
                        LiquidAlertAction("覆盖当前课表", LiquidAlertActionStyle.Destructive) { onConfirm(false) },
                        LiquidAlertAction("取消", LiquidAlertActionStyle.Secondary, onClick = onCancel)
                    ),
                    backdrop = backdrop,
                    config = previewDraft.config
                )
            }
        }
    }
}
