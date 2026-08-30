package com.jobradar.app.presentation.jobs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jobradar.app.domain.model.Sort
import com.jobradar.app.domain.model.salaryLabel
import com.jobradar.app.domain.usecase.JobUi
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients
import com.jobradar.app.presentation.theme.JobRadarMotion
import com.jobradar.app.presentation.ui.components.ErrorState
import com.jobradar.app.presentation.ui.components.TagChip

/**
 * Tab2 Opportunity feed (design spec §5.3).
 *
 * Filter bar + sort chips on top, then a feed of job cards. Each card applies a
 * subtle 3D tilt on press via `graphicsLayer` + a spring bounce. The cards
 * "emerge" with spring physics, matching the design's motion spec.
 */
@Composable
fun JobsScreen(
    onNavigateToJob: (Long) -> Unit,
    viewModel: JobsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onEvent(JobsContract.Event.OnEnter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(JobRadarGradients.BackgroundRadial)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "机会",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = JobRadarColors.TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Sort.entries.forEach { sort ->
                    TagChip(
                        text = sort.label,
                        highlighted = state.filter.sort == sort,
                        onClick = {
                            viewModel.onEvent(
                                JobsContract.Event.OnSortChange(state.filter.copy(sort = sort))
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.error != null && state.jobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    ErrorState(
                        message = state.error.orEmpty(),
                        onRetry = { viewModel.onEvent(JobsContract.Event.Refresh) },
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.jobs, key = { it.job.id }) { jobUi ->
                        TiltJobCard(jobUi = jobUi, onClick = { onNavigateToJob(jobUi.job.id) })
                    }
                }
            }
        }
    }
}

/** A job card with a 3D tilt-on-press effect (design spec §5.3). */
@Composable
private fun TiltJobCard(
    jobUi: JobUi,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val tilt by animateFloatAsState(
        targetValue = if (pressed) 7f else 0f,
        animationSpec = JobRadarMotion.cardSpring(),
        label = "tilt",
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.02f else 1f,
        animationSpec = JobRadarMotion.cardSpring(),
        label = "card-scale",
    )

    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationX = tilt
                scaleX = scale
                scaleY = scale
                cameraDistance = 12f * density
            }
            .clip(shape)
            .background(Brush.linearGradient(listOf(JobRadarColors.SurfaceElevated, JobRadarColors.Surface)))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jobUi.job.title,
                    color = JobRadarColors.TextPrimary,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${jobUi.job.company?.name ?: ""} · ${jobUi.job.city} · ${jobUi.job.salaryLabel()}",
                    color = JobRadarColors.TextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        val tagLine = (jobUi.job.skills.take(3) + listOf(jobUi.job.experience.label, jobUi.job.education.label))
            .filter { it.isNotBlank() }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tagLine.forEach { TagChip(text = it) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "匹配度 ${jobUi.score.total}%",
                color = JobRadarColors.Primary,
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
