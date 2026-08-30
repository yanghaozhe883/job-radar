package com.jobradar.app.presentation.resume

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
import androidx.compose.foundation.rememberScrollState
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
import com.jobradar.app.domain.model.Resume
import com.jobradar.app.domain.model.ResumeProject
import com.jobradar.app.domain.model.ResumeSkill
import com.jobradar.app.presentation.theme.JobRadarColors
import com.jobradar.app.presentation.theme.JobRadarGradients

/** Resume screen — displays the user's finalized resume. */
@Composable
fun ResumeScreen(
    onBack: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onEvent(ResumeContract.Event.OnEnter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(JobRadarGradients.BackgroundRadial)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(JobRadarColors.SurfaceGlassStrong)
                        .clickable { onBack() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text("‹ 返回", color = JobRadarColors.TextPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "我的简历",
                    color = JobRadarColors.TextPrimary,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            val resume = state.resume
            if (resume != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Header(resume)
                    Spacer(modifier = Modifier.height(18.dp))

                    SectionHeader("教育背景")
                    Text(resume.education, color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(18.dp))
                    SectionHeader("核心技能")
                    resume.skills.forEach { skill ->
                        SkillRow(skill)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    SectionHeader("项目经历")
                    resume.projects.forEach { project ->
                        ProjectCard(project)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    SectionHeader("其它 AI 项目")
                    resume.otherProjects.forEach { it ->
                        Bullet(it)
                    }

                    resume.internship?.let {
                        Spacer(modifier = Modifier.height(18.dp))
                        SectionHeader("实习经历")
                        Bullet(it)
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    SectionHeader("校园经历与荣誉")
                    resume.honors.forEach { it ->
                        Bullet(it)
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
private fun Header(resume: Resume) {
    Column {
        Text(resume.name, color = JobRadarColors.TextPrimary, style = androidx.compose.material3.MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(resume.tagline, color = JobRadarColors.Primary, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(resume.contact, color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SectionHeader(text: String) {
    Spacer(modifier = Modifier.height(4.dp))
    Text(text, color = JobRadarColors.Primary, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun SkillRow(skill: ResumeSkill) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(JobRadarColors.SurfaceGlassStrong, JobRadarColors.Surface)))
            .padding(12.dp),
    ) {
        Text(skill.label, color = JobRadarColors.Primary, fontWeight = FontWeight.Bold, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(skill.detail, color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ProjectCard(project: ResumeProject) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(JobRadarColors.SurfaceElevated, JobRadarColors.Surface)))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (project.flagship) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(JobRadarGradients.Glow))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("旗舰", color = JobRadarColors.Background, fontWeight = FontWeight.Bold, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(project.title, color = JobRadarColors.TextPrimary, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(project.meta, color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        project.bullets.forEach { b -> Bullet(b) }
    }
}

@Composable
private fun Bullet(text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("· ", color = JobRadarColors.Primary, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
        Text(text, color = JobRadarColors.TextSecondary, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
    }
}
