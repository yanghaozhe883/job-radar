package com.jobradar.app.presentation.radar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarDp
import com.jobradar.app.presentation.theme.JobRadarMotion
import kotlin.math.cos
import kotlin.math.sin

/**
 * RadarCore (design spec §4.6 / §5.2).
 *
 * A full-screen animated radar that draws:
 *  - concentric rings (transparency fading outward)
 *  - a rotating sweep line + trailing gradient (the "scan")
 *  - a few orbiting blips/particles that pulse
 *
 * The rotation loop is [JobRadarDp.DurationRadarMs] (1600ms) per cycle,
 * matching the design spec exactly. Kept on a pure Canvas + infinite
 * transition so it stays well under the 16ms frame budget.
 */
@Composable
fun RadarCore(
    modifier: Modifier = Modifier,
    detected: Boolean = false,
) {
    val transition = rememberInfiniteTransition(label = "radar")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = JobRadarDp.DurationRadarMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sweep-rotation",
    )
    // A slower counter-rotation for the inner dashed ring — adds depth without noise.
    val innerRotation by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = JobRadarDp.DurationRadarMs * 2, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "inner-rotation",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    // Detection ring: expands outward with a pop-spring when a hit arrives.
    val detectExpand by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (detected) 1f else 0f,
        animationSpec = JobRadarMotion.popSpring(),
        label = "detect-expand",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = minOf(size.width, size.height) * 0.42f

        // Concentric rings
        repeat(3) { i ->
            val r = maxRadius * (1f - i * 0.28f)
            val alpha = 0.5f - i * 0.13f
            drawCircle(
                color = JobRadarColors.Primary.copy(alpha = alpha),
                radius = r,
                center = androidx.compose.ui.geometry.Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }

        // Inner counter-rotating dashed ring (subtle depth accent).
        val dashCount = 24
        val innerR = maxRadius * 0.86f
        val dashSweep = 360f / dashCount
        for (i in 0 until dashCount) {
            val start = innerRotation + (i * dashSweep)
            drawArc(
                color = JobRadarColors.Accent.copy(alpha = 0.28f),
                startAngle = start,
                sweepAngle = dashSweep * 0.5f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(cx - innerR, cy - innerR),
                size = androidx.compose.ui.geometry.Size(innerR * 2, innerR * 2),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        // Outer glow disc (radial gradient)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    JobRadarColors.Primary.copy(alpha = 0.10f * pulse),
                    Color.Transparent,
                ),
                center = androidx.compose.ui.geometry.Offset(cx, cy),
                radius = maxRadius,
            ),
            radius = maxRadius,
            center = androidx.compose.ui.geometry.Offset(cx, cy),
        )

        // Sweep line + trailing wedge (a sweep-gradient fan of light)
        val sweepRad = Math.toRadians(rotation.toDouble())
        val endX = cx + maxRadius * cos(sweepRad).toFloat()
        val endY = cy + maxRadius * sin(sweepRad).toFloat()
        drawLine(
            color = JobRadarColors.Primary.copy(alpha = 0.9f),
            start = androidx.compose.ui.geometry.Offset(cx, cy),
            end = androidx.compose.ui.geometry.Offset(endX, endY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        // Sweep gradient wedge — a fan of light trailing behind the scan line.
        drawArc(
            brush = Brush.sweepGradient(
                0.0f to Color.Transparent,
                0.35f to JobRadarColors.Primary.copy(alpha = 0.45f * pulse),
                0.75f to JobRadarColors.Primary.copy(alpha = 0.18f * pulse),
                1.0f to Color.Transparent,
                center = androidx.compose.ui.geometry.Offset(cx, cy),
            ),
            startAngle = rotation - 110f,
            sweepAngle = 110f,
            useCenter = true,
        )
        // A few particles scattered behind the sweep (varying alpha) — the "optics" feel.
        repeat(7) { i ->
            val spread = i * 0.5f + 1f
            val angle = rotation - (10f + i * 9f)
            val rad = Math.toRadians(angle.toDouble())
            val r = maxRadius * (0.35f + 0.5f * (((i * 37) % 10) / 10f))
            val px = cx + r * cos(rad).toFloat()
            val py = cy + r * sin(rad).toFloat()
            val alpha = (1f - i / 7f) * 0.6f * pulse
            drawCircle(
                color = JobRadarColors.Primary.copy(alpha = alpha.coerceIn(0f, 0.8f)),
                radius = (1.2f + ((i * 7) % 3)).dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(px, py),
            )
        }

        // Blips (particles) orbiting
        val blipAngle = Math.toRadians((rotation * 2.4f).toDouble())
        val blipRadius = maxRadius * 0.62f
        val bx = cx + blipRadius * cos(blipAngle).toFloat()
        val by = cy + blipRadius * sin(blipAngle).toFloat()
        // pulse node
        drawCircle(
            color = JobRadarColors.Accent.copy(alpha = 0.9f * pulse),
            radius = 4.dp.toPx() * pulse,
            center = androidx.compose.ui.geometry.Offset(bx, by),
        )
        // center node
        drawCircle(
            color = JobRadarColors.Primary.copy(alpha = 1f),
            radius = 5.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(cx, cy),
        )

        // detection flash when a new opportunity is captured
        if (detected) {
            // Expanding ring that grows outward and fades — the "catch" moment.
            val expandRadius = maxRadius * (0.3f + 0.9f * detectExpand)
            drawCircle(
                color = JobRadarColors.Success.copy(alpha = (1f - detectExpand).coerceIn(0f, 0.5f)),
                radius = expandRadius,
                center = androidx.compose.ui.geometry.Offset(cx, cy),
                style = Stroke(width = 3.dp.toPx()),
            )
            // Keep a soft inner glow while detected.
            drawCircle(
                color = JobRadarColors.Success.copy(alpha = 0.12f * pulse),
                radius = maxRadius * 0.9f,
                center = androidx.compose.ui.geometry.Offset(cx, cy),
            )
        }
    }
}
