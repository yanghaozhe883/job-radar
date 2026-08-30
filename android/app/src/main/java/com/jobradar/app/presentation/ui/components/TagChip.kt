package com.jobradar.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jobradar.app.presentation.theme.JobRadarColors

/**
 * A small chip / tag used for skills, salary, job type etc. (design spec §4.4).
 * Supports a "selected/emphasis" variant.
 */
@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    val base = modifier
        .widthIn(min = 0.dp)
        .padding(horizontal = 4.dp)
        .background(
            brush = if (highlighted) {
                Brush.linearGradient(listOf(JobRadarColors.Primary, JobRadarColors.PrimaryDark))
            } else {
                Brush.linearGradient(listOf(JobRadarColors.Surface, JobRadarColors.SurfaceElevated))
            },
            shape = shape,
        )
        .border(1.dp, JobRadarColors.Border, shape)
        .padding(horizontal = 10.dp, vertical = 5.dp)

    Box(
        modifier = if (onClick != null) base.clickable(onClick = onClick) else base,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (highlighted) Color(0xFF00201B) else JobRadarColors.TextSecondary,
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

/** A horizontal flow of [TagChip]s. */
@Composable
fun TagRow(
    tags: List<String>,
    modifier: Modifier = Modifier,
    highlightedIndex: Int? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tags.forEachIndexed { index, label ->
            TagChip(
                text = label,
                highlighted = highlightedIndex == index,
            )
        }
    }
}
