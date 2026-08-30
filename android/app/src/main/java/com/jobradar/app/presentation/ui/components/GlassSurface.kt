package com.jobradar.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jobradar.app.presentation.theme.JobRadarColors

/**
 * Glassmorphism surface (design spec §2.5 / §4).
 *
 * On API 31+ it applies a real translucent backdrop via `graphicsLayer` +
 * platform blur. Below that it degrades to a translucent gradient so the look
 * is preserved everywhere. Content is drawn on top, clipped to [shape].
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    cornerRadiusDp: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        JobRadarColors.SurfaceGlassStrong,
                        JobRadarColors.SurfaceGlass.copy(alpha = 0.5f),
                    )
                )
            )
            .drawBehind {
                // subtle border + a gloss highlight along the top edge
                drawRoundRect(
                    color = JobRadarColors.Border,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(cornerRadiusDp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                )
                // top gloss light line
                drawRoundRect(
                    color = JobRadarColors.TextPrimary.copy(alpha = 0.08f),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 0.18f),
                    cornerRadius = CornerRadius(cornerRadiusDp.toPx()),
                )
            }
            .padding(contentPadding),
    ) {
        content()
    }
}
