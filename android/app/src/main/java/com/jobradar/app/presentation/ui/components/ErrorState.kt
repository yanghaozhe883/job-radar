package com.jobradar.app.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jobradar.app.presentation.theme.JobRadarColors

/**
 * A friendly offline/error state shown when the backend is unreachable, with a
 * retry action so the user isn't left staring at an empty screen.
 */
@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    message: String = "无法连接服务器，请检查网络后重试",
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiOff,
            contentDescription = null,
            tint = JobRadarColors.TextTertiary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = JobRadarColors.TextSecondary,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "重试",
            color = JobRadarColors.Primary,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            modifier = Modifier.clickable { onRetry() },
        )
    }
}
