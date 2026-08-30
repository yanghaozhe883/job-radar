package com.jobradar.app.presentation.radar

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients
import com.jobradar.app.presentation.ui.components.JobCard

/**
 * Tab1 Radar screen (design spec §5.2).
 *
 * Full-screen radar canvas with the scan sweep, a status line, and a stream of
 * "detected" opportunities that emerge as cards. Uses the radar HIT state to
 * trigger the detection flash + card reveal.
 */
@Composable
fun RadarScreen(
    onNavigateToJob: (Long) -> Unit,
    onNavigateToAi: () -> Unit = {},
    viewModel: RadarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    // Consume one-shot effects: navigation, haptic, and toast.
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RadarContract.Effect.NavigateToJob -> onNavigateToJob(effect.jobId)
                is RadarContract.Effect.VibrateOnDetection -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                is RadarContract.Effect.Toast -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(RadarContract.Event.OnEnter)
    }

    Scaffold(
        containerColor = JobRadarColors.Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.linearGradient(JobRadarGradients.BackgroundRadial)
                ),
        ) {
        // The radar canvas fills the background.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, bottom = 260.dp),
            contentAlignment = Alignment.Center,
        ) {
            RadarCore(modifier = Modifier.fillMaxSize(), detected = state.lastDetectedJobId != null)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // Header status
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "求职雷达",
                    modifier = Modifier.weight(1f),
                    color = JobRadarColors.TextPrimary,
                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                )
                // AI assistant entry (knowledge-base backed)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(JobRadarColors.SurfaceGlassStrong)
                        .clickable { onNavigateToAi() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "AI 助手",
                        color = JobRadarColors.Primary,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "正在盯 · ${state.preference.city} · ${state.preference.targetRoles.joinToString("/")}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                color = JobRadarColors.Primary,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(180.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (state.liveSignal != null || state.lastDetectedJobId != null) "检测到新机会" else "雷达扫描中…",
                    color = JobRadarColors.TextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }

            // Detected opportunities (emerging cards)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.radarHits) { hit ->
                    JobCard(
                        job = hit.job,
                        matchScore = hit.score.total,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToJob(hit.job.id) },
                    )
                }
            }
        }
        }
    }
}
