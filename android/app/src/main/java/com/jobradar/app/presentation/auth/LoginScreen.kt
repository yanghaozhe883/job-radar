package com.jobradar.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients
import com.jobradar.app.presentation.ui.components.PrimaryButton

/**
 * Login / splash screen (design spec §5.1). Brand + phone + verification code
 * + primary CTA. On successful sign-in it emits [LoginContract.Effect.LoginSuccess]
 * which the navigation layer uses to enter the app.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginContract.Effect.LoginSuccess -> onLoginSuccess()
                is LoginContract.Effect.Toast -> { /* snackbar/Toast could be shown here */ }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(JobRadarGradients.BackgroundRadial)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
        ) {
            Spacer(modifier = Modifier.height(96.dp))
            // Brand
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(JobRadarColors.Primary, JobRadarColors.Accent))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Radar,
                    contentDescription = "求职雷达",
                    tint = JobRadarColors.Background,
                    modifier = Modifier.size(40.dp),
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "求职雷达",
                color = JobRadarColors.TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "点亮雷达，让好机会主动找到你",
                color = JobRadarColors.TextSecondary,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onEvent(LoginContract.Event.OnPhoneChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("手机号") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp),
                colors = loginFieldColors(),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.code,
                    onValueChange = { viewModel.onEvent(LoginContract.Event.OnCodeChange(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("验证码") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    colors = loginFieldColors(),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(JobRadarColors.SurfaceGlassStrong)
                        .padding(horizontal = 14.dp, vertical = 18.dp),
                ) {
                    Text(
                        text = "获取验证码",
                        color = JobRadarColors.Primary,
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = state.error.orEmpty(),
                    color = JobRadarColors.Danger,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = if (state.isLoading) "登录中…" else "登录",
                onClick = { viewModel.onEvent(LoginContract.Event.SignIn) },
            )
        }
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = JobRadarColors.Primary,
    unfocusedBorderColor = JobRadarColors.Border,
    focusedLabelColor = JobRadarColors.Primary,
    unfocusedLabelColor = JobRadarColors.TextSecondary,
    cursorColor = JobRadarColors.Primary,
    focusedTextColor = JobRadarColors.TextPrimary,
    unfocusedTextColor = JobRadarColors.TextPrimary,
)
