package com.jobradar.app.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients
import com.jobradar.app.presentation.ui.components.JobCard

/** Tab3 Favorites screen (design spec §5.5). */
@Composable
fun FavoritesScreen(
    onNavigateToJob: (Long) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onEvent(FavoritesContract.Event.OnEnter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(JobRadarGradients.BackgroundRadial)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "收藏",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = JobRadarColors.TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.jobs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkBorder,
                            contentDescription = null,
                            tint = JobRadarColors.TextTertiary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "还没有收藏的机会",
                            color = JobRadarColors.TextSecondary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "在职位详情点击收藏，雷达会为你持续跟进",
                            color = JobRadarColors.TextTertiary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.jobs, key = { it.job.id }) { jobUi ->
                        JobCard(
                            job = jobUi.job,
                            matchScore = jobUi.score.total,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToJob(jobUi.job.id) },
                        )
                    }
                }
            }
        }
    }
}
