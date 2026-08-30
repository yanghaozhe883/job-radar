package com.jobradar.app.presentation.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients
import com.jobradar.app.presentation.ui.components.PrimaryButton
import com.jobradar.app.presentation.ui.components.TagChip

/** The available roles toggled for the radar. */
private val roles = listOf(
    "前端工程师", "Android 工程师", "iOS 开发工程师", "算法工程师",
    "产品经理", "数据分析师", "测试工程师", "全栈开发",
)
private val cities = listOf("上海", "北京", "深圳", "杭州", "广州")

/** Tab4 Profile / preferences screen (design spec §5.6). */
@Composable
fun ProfileScreen(
    onNavigateToResume: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onEvent(ProfileContract.Event.OnEnter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(JobRadarGradients.BackgroundRadial)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(JobRadarColors.Accent, JobRadarColors.Primary)))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "R",
                        color = JobRadarColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(
                        text = "求职者",
                        color = JobRadarColors.TextPrimary,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "雷达目标配置",
                        color = JobRadarColors.TextSecondary,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            SectionTitle("目标城市")
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                cities.forEach { city ->
                    TagChip(
                        text = city,
                        highlighted = state.preference.city == city,
                        onClick = { viewModel.onEvent(ProfileContract.Event.OnCityChange(city)) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            SectionTitle("目标岗位")
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                roles.chunked(2).forEach { rowRoles ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowRoles.forEach { role ->
                            TagChip(
                                text = role,
                                highlighted = role in state.preference.targetRoles,
                                onClick = { viewModel.onEvent(ProfileContract.Event.OnRoleToggle(role)) },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(JobRadarColors.SurfaceGlassStrong)
                .padding(16.dp)) {
                Text(
                    text = "雷达将按以上城市与岗位持续扫描新职位，并在检测到高匹配机会时第一时间提醒你。",
                    color = JobRadarColors.TextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            // Resume entry — show the user's own resume in-app.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(JobRadarColors.SurfaceGlassStrong, JobRadarColors.Surface)))
                    .clickable { onNavigateToResume() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "我的简历",
                    color = JobRadarColors.Primary,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                text = "保存雷达配置",
                onClick = { viewModel.onEvent(ProfileContract.Event.OnSavePreference(state.preference)) },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(JobRadarColors.SurfaceGlassStrong)
                    .clickable { viewModel.onEvent(ProfileContract.Event.SignOut) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "退出登录",
                    color = JobRadarColors.Danger,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = JobRadarColors.TextPrimary,
        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}
