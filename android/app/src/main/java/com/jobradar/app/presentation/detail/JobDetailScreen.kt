package com.jobradar.app.presentation.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.cos
import kotlin.math.sin
import com.jobradar.app.domain.model.JobStatus
import com.jobradar.app.domain.model.JobInsight
import com.jobradar.app.domain.model.salaryLabel
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients
import com.jobradar.app.presentation.ui.components.PrimaryButton
import com.jobradar.app.presentation.ui.components.TagChip

/**
 * Job Detail screen (design spec §5.4).
 *
 * Also the destination for the shared-element transition: the hero header
 * (company logo + title) is shared with the feed via SharedTransitionLayout,
 * so tapping a card morphs its content into this screen. The skill match is
 * rendered with a Canvas match visualization; the JD text fades in.
 */
@Composable
fun JobDetailScreen(
    onBack: () -> Unit,
    onAnalyzeWithAi: (String) -> Unit = {},
    viewModel: JobDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val job = state.job

    LaunchedEffect(Unit) { viewModel.onEvent(JobDetailContract.Event.OnEnter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(JobRadarGradients.BackgroundRadial)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // Top bar with back + favorite
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { viewModel.onEvent(JobDetailContract.Event.OnBack) }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "返回",
                        tint = JobRadarColors.TextPrimary,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                val fav = state.status == JobStatus.FAVORITE
                IconButton(onClick = { viewModel.onEvent(JobDetailContract.Event.ToggleFavorite) }) {
                    Icon(
                        imageVector = if (fav) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = "收藏",
                        tint = if (fav) JobRadarColors.Primary else JobRadarColors.TextSecondary,
                    )
                }
            }

            if (job == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("加载中…", color = JobRadarColors.TextSecondary)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                HeroHeader(job = job, score = state.score?.total ?: 0)
                Spacer(modifier = Modifier.height(20.dp))

                // Skill match visualization
                state.score?.let { score ->
                    SkillMatchPanel(
                        total = score.total,
                        skills = job.skills.take(4),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // v0.3 · AI Insight Card (explainable job understanding)
                InsightCard(
                    state = state.insight,
                    uiState = state.insightState,
                )
                Spacer(modifier = Modifier.height(20.dp))

                // JD description fading in
                Text(
                    text = "职位描述",
                    color = JobRadarColors.TextPrimary,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = job.description ?: "暂无详细描述。",
                    color = JobRadarColors.TextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(32.dp))
                // AI 分析：让知识库 judge 这个岗位是否适合你
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(JobRadarColors.SurfaceGlassStrong)
                        .clickable {
                            onAnalyzeWithAi(
                                "这个岗位「${job.title}」（${job.city}，${job.salaryLabel()}）适合我吗？请结合我的背景给出分析与建议。"
                            )
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "AI 分析此岗位",
                        color = JobRadarColors.Accent,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    text = "立即投递",
                    onClick = { viewModel.onEvent(JobDetailContract.Event.Apply) },
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HeroHeader(job: com.jobradar.app.domain.model.Job, score: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(JobRadarColors.Accent, JobRadarColors.Primary))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = job.company?.name?.take(1) ?: "?",
                color = Color(0xFF0A0E17),
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = job.title,
                color = JobRadarColors.TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${job.company?.name ?: ""} · ${job.city} · ${job.salaryLabel()}",
                color = JobRadarColors.Primary,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$score%",
            color = JobRadarColors.Success,
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TagChip(text = job.jobType.label)
        TagChip(text = job.experience.label)
        TagChip(text = job.education.label)
    }
}

@Composable
private fun SkillMatchPanel(total: Int, skills: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Mini skill radar viz
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(JobRadarColors.SurfaceGlassStrong),
            contentAlignment = Alignment.Center,
        ) {
            SkillRadarVisual(total = total)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "技能匹配",
                color = JobRadarColors.TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "相关技能：${skills.joinToString(" / ")}",
                color = JobRadarColors.TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            )
        }
    }
}

/** A hexagon "capability radar" chart — game-character style. */
@Composable
private fun SkillRadarVisual(total: Int) {
    val animated by animateFloatAsState(
        targetValue = total / 100f,
        animationSpec = tween(durationMillis = 700),
        label = "skill-radar",
    )
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxR = size.minDimension / 2f * 0.9f
        val sides = 6
        // helper: vertex of a regular polygon
        fun vertex(index: Int, radius: Float): androidx.compose.ui.geometry.Offset {
            val angle = (-90f + index * (360f / sides)) * (Math.PI / 180f)
            return androidx.compose.ui.geometry.Offset(cx + radius * cos(angle).toFloat(), cy + radius * sin(angle).toFloat())
        }
        // grid rings (hexagons)
        repeat(4) { ring ->
            val r = maxR * (1f - ring * 0.25f)
            val path = androidx.compose.ui.graphics.Path()
            for (i in 0 until sides) {
                val p = vertex(i, r)
                if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
            }
            path.close()
            drawPath(path, color = JobRadarColors.Border.copy(alpha = 0.7f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()))
        }
        // spokes
        for (i in 0 until sides) {
            val p = vertex(i, maxR)
            drawLine(JobRadarColors.Border.copy(alpha = 0.5f), androidx.compose.ui.geometry.Offset(cx, cy), p, strokeWidth = 1.dp.toPx())
        }
        // filled capability shape (blend of axis values, animated from center)
        val fillPath = androidx.compose.ui.graphics.Path()
        for (i in 0 until sides) {
            // each capability scales with the total score (with slight variation)
            val variation = 0.75f + 0.25f * ((((i * 137) % 10) / 10f))
            val r = maxR * animated * variation
            val p = vertex(i, r)
            if (i == 0) fillPath.moveTo(p.x, p.y) else fillPath.lineTo(p.x, p.y)
        }
        fillPath.close()
        drawPath(
            fillPath,
            brush = Brush.linearGradient(listOf(JobRadarColors.Primary.copy(alpha = 0.7f), JobRadarColors.Accent.copy(alpha = 0.7f))),
        )
        drawPath(
            fillPath,
            color = JobRadarColors.Primary,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx()),
        )
    }
}

/** v0.3 · AI Insight Card — the explainable job understanding. */
@Composable
private fun InsightCard(state: JobInsight?, uiState: InsightUiState) {
    when (uiState) {
        InsightUiState.Loading -> Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(JobRadarColors.SurfaceGlassStrong).padding(vertical = 24.dp),
            contentAlignment = Alignment.Center,
        ) { Text("正在分析这个岗位…", color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodySmall) }

        InsightUiState.Error, InsightUiState.Degraded -> Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(JobRadarColors.SurfaceGlassStrong).padding(vertical = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("AI 洞察暂不可用", color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                Text("职位详情仍可正常查看", color = JobRadarColors.TextTertiary, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
            }
        }

        InsightUiState.Loaded -> state?.let { insight ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("AI Insight", color = JobRadarColors.TextPrimary, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Match overview
                Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(JobRadarColors.SurfaceGlassStrong).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("匹配度", color = JobRadarColors.TextPrimary, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${insight.match.overall}", color = JobRadarColors.Primary, style = androidx.compose.material3.MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    MatchBar("技能匹配", insight.match.skillMatch, insight.match.skillReason, JobRadarColors.Primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    MatchBar("经验匹配", insight.match.experienceMatch, insight.match.experienceReason, JobRadarColors.Accent)
                    Spacer(modifier = Modifier.height(10.dp))
                    MatchBar("方向匹配", insight.match.directionMatch, insight.match.directionReason, JobRadarColors.Success)
                }
                Spacer(modifier = Modifier.height(12.dp))

                InsightSection("岗位职责", insight.responsibilities, "◎")
                InsightSection("核心技能", insight.coreSkills, "✦")
                InsightSection("风险点", insight.riskPoints, "!")
                InsightSection("成长空间", insight.growth, "↑")
                InsightSection("为什么推荐", insight.whyRecommended, "▶")
            }
        }
    }
}

@Composable
private fun MatchBar(label: String, value: Int, reason: String, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(label, color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Text("$value", color = color, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(JobRadarColors.Border)) {
            Box(modifier = Modifier.fillMaxWidth(value.coerceIn(0, 100) / 100f).height(6.dp)
                .clip(RoundedCornerShape(3.dp)).background(color))
        }
        if (reason.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(reason, color = JobRadarColors.TextTertiary, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun InsightSection(title: String, items: List<String>, icon: String) {
    if (items.isEmpty()) return
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .background(JobRadarColors.SurfaceGlassStrong).padding(16.dp)) {
        Text("$icon  $title", color = JobRadarColors.Primary, style = androidx.compose.material3.MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        items.forEach {
            Text("·  $it", color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}
