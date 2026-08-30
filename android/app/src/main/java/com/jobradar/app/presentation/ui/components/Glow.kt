package com.jobradar.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Colored ambient "glow" (design spec §2.5): a soft outer halo of [glowColor]
 * plus a subtle top-edge highlight ring. The key difference from a plain shadow
 * is that the glow is *colored* and *sits outside* the shape, giving that
 * cyberpunk / neon "signal" feel instead of a black drop shadow.
 *
 * Usage:
 *   Modifier.glow(color = JobRadarGlow.Primary, shape = RoundedCornerShape(26.dp))
 */
fun Modifier.glow(glowColor: Color, haloAlpha: Float = 0.40f): Modifier = this.drawBehind {
    val radius = size.minDimension
    // soft outer halo (semi-transparent color around the shape)
    drawRoundRect(
        color = glowColor.copy(alpha = haloAlpha),
        topLeft = Offset(-size.minDimension * 0.15f, -size.minDimension * 0.15f),
        size = Size(size.width + size.minDimension * 0.3f, size.height + size.minDimension * 0.3f),
        cornerRadius = CornerRadius(radius * 0.42f),
    )
    // crisp inner ring for definition
    drawRoundRect(
        color = glowColor.copy(alpha = 0.85f),
        topLeft = Offset.Zero,
        size = Size(size.width, size.height),
        cornerRadius = CornerRadius(radius * 0.42f),
        style = Stroke(width = 1.5.dp.toPx()),
    )
}
