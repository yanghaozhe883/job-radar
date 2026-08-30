package com.jobradar.app.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients
import kotlin.math.min

/**
 * Match score ring (design spec §4.3).
 *  - Circular progress ring with grad-glow stroke.
 *  - Color maps to score band: >=80 success / 60-79 primary / 40-59 warning / <40 muted.
 *  - Animates 0 -> target on appearance.
 */
@Composable
fun MatchRing(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 4.dp,
) {
    val target = (score / 100f).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
        label = "ring-progress",
    )

    val color = when {
        score >= 80 -> JobRadarColors.Success
        score >= 60 -> JobRadarColors.Primary
        score >= 40 -> JobRadarColors.Warning
        else -> JobRadarColors.TextTertiary
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size).drawBehind {
            // colored glow ring behind the progress arc (the "signal lit up" feel)
            drawCircle(
                color = color.copy(alpha = 0.35f),
                radius = this.size.minDimension * 0.30f,
                center = this.center,
            )
        }) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // track
            drawArc(
                color = JobRadarColors.Border,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
            )
            // progress with a subtle glow
            val brush = Brush.sweepGradient(
                listOf(JobRadarColors.Primary, color, JobRadarColors.Accent),
                center = center,
            )
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                style = stroke,
            )
        }
        Text(
            text = score.toString(),
            color = color,
            fontSize = (min(size.value, 56.dp.value) * 0.34f).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
