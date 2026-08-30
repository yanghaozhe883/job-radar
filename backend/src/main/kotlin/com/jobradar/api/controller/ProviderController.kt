package com.jobradar.api.controller

import com.jobradar.api.ApiResponse
import com.jobradar.provider.JobProviderRegistry
import com.jobradar.provider.JobSearchFilter
import com.jobradar.provider.ProviderJob
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * A thin read-only view over the pluggable [com.jobradar.provider.JobProvider].
 * Lets clients get jobs straight from the active provider (mock today, CSV/API
 * tomorrow) without touching the ingestion/DB path — the "product reads through
 * the seam" proof.
 *
 *   GET /api/v1/provider/jobs?city=&keyword=&page=1&pageSize=10
 */
@RestController
@RequestMapping("\${api.base-path:/api/v1}/provider")
class ProviderController(
    private val registry: JobProviderRegistry,
) {

    @GetMapping("/jobs")
    fun jobs(
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false, name = "job_type") jobType: String?,
        @RequestParam(required = false, name = "min_salary_k") minSalaryK: Int?,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): ApiResponse<Map<String, Any>> {
        val provider = registry.active
        val jobs = provider.search(JobSearchFilter(city, keyword, jobType, minSalaryK, page, pageSize))
        return ApiResponse.ok(
            mapOf(
                "provider" to provider.key,
                "total" to provider.count(),
                "items" to jobs,
            )
        )
    }

    @GetMapping("/detail")
    fun detail(@RequestParam id: String): ApiResponse<ProviderJob?> =
        ApiResponse.ok(registry.active.detail(id))
}
