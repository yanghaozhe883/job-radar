package com.jobradar.api.controller

import com.jobradar.api.ApiCode
import com.jobradar.api.ApiException
import com.jobradar.api.ApiResponse
import com.jobradar.domain.JobInsight
import com.jobradar.insight.InsightJob
import com.jobradar.insight.InsightService
import com.jobradar.insight.UserProfile
import com.jobradar.provider.JobProviderRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * v0.3 · Insight endpoint.
 *   GET /api/v1/jobs/{id}/insight
 *
 * Reads the job through the SAME JobProvider as /jobs (single source of truth),
 * then asks InsightService (which never knows the AI details).
 */
@RestController
@RequestMapping("\${api.base-path:/api/v1}")
class JobInsightController(
    private val registry: JobProviderRegistry,
    private val insightService: InsightService,
) {

    @GetMapping("/jobs/{id}/insight")
    fun insight(
        @PathVariable id: String,
        @RequestParam(required = false, name = "target_roles") targetRoles: List<String>?,
        @RequestParam(required = false) skills: List<String>?,
        @RequestParam(required = false, name = "years_of_experience") yearsOfExperience: Int?,
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
        val profile = UserProfile(
            targetRoles = targetRoles ?: emptyList(),
            skills = skills ?: emptyList(),
            yearsOfExperience = yearsOfExperience ?: 0,
        )
        return ApiResponse.ok(insightService.generate(job, profile))
    }
}
