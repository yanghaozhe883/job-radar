package com.jobradar.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.salaryLabel
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarDp
import com.jobradar.app.presentation.theme.JobRadarGradients
import java.util.Locale

/**
 * Job card (design spec §4.2). Glass surface + company logo + title + tags +
 * match ring. Used in both the opportunity feed and the radar screen.
 */
@Composable
fun JobCard(
    job: Job,
    matchScore: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .clip(shape)
            // glassy translucent fill + a top "gloss" light band
            .background(
                Brush.verticalGradient(
                    listOf(
                        JobRadarColors.SurfaceGlassStrong,   // top highlight (gloss)
                        JobRadarColors.SurfaceElevated.copy(alpha = 0.9f),
                        JobRadarColors.Surface.copy(alpha = 0.85f),
                    )
                )
            )
            .border(1.dp, JobRadarColors.Border, shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CompanyLogo(job.company?.name ?: "?")
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.title,
                    color = JobRadarColors.TextPrimary,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.company?.name ?: "",
                    color = JobRadarColors.TextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Work,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = JobRadarColors.Primary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.salaryLabel(),
                        color = JobRadarColors.Primary,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = JobRadarColors.TextTertiary,
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = job.city,
                        color = JobRadarColors.TextSecondary,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val skillsPreview = job.skills.take(2)
                    skillsPreview.forEach { skill ->
                        TagChip(text = skill, highlighted = false)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            MatchRing(score = matchScore, size = 52.dp)
        }
    }
}

private fun titleInitial(name: String): String = name.take(1).uppercase(Locale.getDefault())

@Composable
private fun CompanyLogo(name: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(JobRadarColors.Accent, JobRadarColors.Primary))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = titleInitial(name),
            color = Color(0xFF0A0E17),
            fontWeight = FontWeight.Bold,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
        )
    }
}
