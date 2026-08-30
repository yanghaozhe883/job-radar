package com.jobradar.api.controller

import com.jobradar.api.ApiResponse
import com.jobradar.api.dto.JobDto
import com.jobradar.api.dto.PageDto
import com.jobradar.api.dto.SortOption
import com.jobradar.service.JobService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Job endpoints. Matches the Android client's `JobApiService` exactly:
 *   GET /api/v1/jobs
 *   GET /api/v1/jobs/{id}
 *   GET /api/v1/jobs/radar/hits
 */
@RestController
@RequestMapping("\${api.base-path:/api/v1}/jobs")
class JobController(
    private val jobService: JobService,
) {

    @GetMapping
    fun getJobs(
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false, name = "job_type") jobType: String?,
        @RequestParam(required = false, name = "min_salary_k") minSalaryK: Int?,
        @RequestParam(required = false) sort: String?,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ApiResponse<PageDto<JobDto>> {
        val data = jobService.search(city, keyword, jobType, minSalaryK, SortOption.from(sort), page, pageSize)
        return ApiResponse.ok(data)
    }

    @GetMapping("/{id}")
    fun getJob(@PathVariable id: Long): ApiResponse<JobDto> =
        ApiResponse.ok(jobService.getById(id))

    @GetMapping("/radar/hits")
    fun getRadarHits(
        @RequestParam(required = false, defaultValue = "5") count: Int,
        @RequestParam(required = false, name = "user_id") userId: Long?,
        @RequestParam(required = false) keywords: List<String>?,
    ): ApiResponse<List<JobDto>> =
        ApiResponse.ok(jobService.radarHits(count, userId, keywords))
}
