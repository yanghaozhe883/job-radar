package com.jobradar.app.presentation.ai

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients

/**
 * AI assistant screen — a chat backed by the local knowledge base.
 * The backend holds the AnythingLLM key; this screen only sends the question.
 */
@Composable
fun AiAssistantScreen(
    initialQuestion: String? = null,
    viewModel: AiViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(initialQuestion) {
        if (!initialQuestion.isNullOrBlank() && state.messages.isEmpty()) {
            viewModel.onEvent(AiContract.Event.OnInputChange(initialQuestion))
            viewModel.onEvent(AiContract.Event.Send)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(JobRadarGradients.BackgroundRadial)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "AI 求职助手",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = JobRadarColors.TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "基于你的知识库回答（后端安全持有密钥）",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = JobRadarColors.TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.messages.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "向你的知识库提问吧，例如：\n“我该补哪些技能 / 帮我准备面试”",
                        color = JobRadarColors.TextTertiary,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.messages) { msg ->
                        MessageBubble(msg)
                    }
                }
            }

            // Input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material3.OutlinedTextField(
                    value = state.input,
                    onValueChange = { viewModel.onEvent(AiContract.Event.OnInputChange(it)) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("问你的知识库…", color = JobRadarColors.TextTertiary) },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(JobRadarColors.Primary, JobRadarColors.PrimaryDark)))
                        .clickable { viewModel.onEvent(AiContract.Event.Send) }
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = if (state.isLoading) "…" else "发送",
                        color = JobRadarColors.Background,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: AiContract.Message) {
    val bubbleColor = if (msg.fromUser) JobRadarColors.Primary else JobRadarColors.SurfaceElevated
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.fromUser) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(12.dp),
        ) {
            Text(
                text = msg.text,
                color = if (msg.fromUser) JobRadarColors.Background else JobRadarColors.TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            )
            if (msg.sources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "来源: ${msg.sources.joinToString(", ")}",
                    color = if (msg.fromUser) JobRadarColors.Background else JobRadarColors.TextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
