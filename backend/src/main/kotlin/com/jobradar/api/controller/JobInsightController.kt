package com.jobradar.api.controller

import com.jobradar.api.ApiCode
import com.jobradar.api.ApiException
import com.jobradar.api.ApiResponse
import com.jobradar.domain.JobInsight
import com.jobradar.insight.InsightJob
import com.jobradar.insight.InsightService
import com.jobradar.insight.UserProfileProvider
import com.jobradar.provider.JobProviderRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * v0.3 · Insight endpoint.
 *   GET /api/v1/jobs/{id}/insight?user_id={userId}
 *
 * Grounding rule: **Client chooses the job, Backend owns the user context.**
 * The job comes from the SAME JobProvider as /jobs (single source of truth);
 * the user profile comes from the BACKEND (UserProfileProvider reading the
 * saved preference) — NOT from client-supplied skills/years params. Missing
 * user info stays empty, so the model can't invent facts the user never stated.
 */
@RestController
@RequestMapping("\${api.base-path:/api/v1}")
class JobInsightController(
    private val registry: JobProviderRegistry,
    private val insightService: InsightService,
    private val userProfileProvider: UserProfileProvider,
) {

    @GetMapping("/jobs/{id}/insight")
    fun insight(
        @PathVariable id: String,
        @RequestParam(required = false, name = "user_id") userId: Long?,
    ): ApiResponse<JobInsight> {
        val p = registry.active.detail(id)
            ?: throw ApiException(ApiCode.NOT_FOUND, "职位不存在")
        val job = InsightJob(
            id = p.id,
            title = p.title,
            city = p.city,
            salaryMinK = p.salaryMinK,
            salaryMaxK = p.salaryMaxK,
            jobType = p.jobType,
            experience = p.experience,
            education = p.education,
            skills = p.skills,
            description = p.description,
            companyName = p.companyName,
        )
        // Backend owns the user context — read from saved preference, not client.
        val profile = userProfileProvider.profileFor(userId)
        return ApiResponse.ok(insightService.generate(job, profile))
    }
}
