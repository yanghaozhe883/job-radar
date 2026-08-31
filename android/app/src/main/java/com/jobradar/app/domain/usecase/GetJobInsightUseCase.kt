package com.jobradar.app.domain.usecase

import com.jobradar.app.data.remote.JobApiService
import com.jobradar.app.data.remote.dto.JobInsightDto
import com.jobradar.app.domain.model.JobInsight
import com.jobradar.app.domain.model.MatchInsight
import javax.inject.Inject

/** v0.3 · Fetch the explainable insight for a job (from the backend contract). */
class GetJobInsightUseCase @Inject constructor(
    private val api: JobApiService,
) {
    suspend operator fun invoke(
        jobId: String,
        targetRoles: List<String>? = null,
        skills: List<String>? = null,
        yearsOfExperience: Int? = null,
    ): JobInsight? = runCatching {
        val dto = api.getInsight(jobId, targetRoles, skills, yearsOfExperience).requireData()
        dto.toDomain()
    }.getOrNull()
}

private fun JobInsightDto.toDomain() = JobInsight(
    jobId = jobId,
    responsibilities = responsibilities,
    coreSkills = coreSkills,
    riskPoints = riskPoints,
    growth = growth,
    whyRecommended = whyRecommended,
    match = MatchInsight(
        skillMatch = match.skillMatch,
        experienceMatch = match.experienceMatch,
        directionMatch = match.directionMatch,
        skillReason = match.skillReason,
        experienceReason = match.experienceReason,
        directionReason = match.directionReason,
    ),
    model = model,
    generatedBy = generatedBy,
)
