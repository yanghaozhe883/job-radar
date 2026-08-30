package com.jobradar.api.controller

import com.jobradar.api.ApiCode
import com.jobradar.api.ApiException
import com.jobradar.api.ApiResponse
import com.jobradar.api.dto.CompanyDto
import com.jobradar.api.dto.JobDto
import com.jobradar.api.dto.PageDto
import com.jobradar.provider.JobProviderRegistry
import com.jobradar.provider.JobSearchFilter
import com.jobradar.provider.ProviderMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Canonical product API (v0.2 · Connect).
 *
 * Every client reads jobs ONLY through the [JobProviderRegistry] — a client
 * never knows whether data comes from Mock / CSV / a plugin. The backend is the
 * only thing that knows about [com.jobradar.provider.JobProvider]s.
 *
 *   GET /api/v1/jobs?city=&keyword=&page=&pageSize=
 *   GET /api/v1/jobs/{id}
 *   GET /api/v1/companies/{name}
 *   GET /api/v1/providers
 */
@RestController
@RequestMapping("\${api.base-path:/api/v1}")
class ProductApiController(
    private val registry: JobProviderRegistry,
) {

    @GetMapping("/jobs")
    fun listJobs(
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false, name = "job_type") jobType: String?,
        @RequestParam(required = false, name = "min_salary_k") minSalaryK: Int?,
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "20") pageSize: Int,
    ): ApiResponse<PageDto<JobDto>> {
        val provider = registry.active
        val jobs = provider.search(JobSearchFilter(city, keyword, jobType, minSalaryK, page, pageSize))
        val total = provider.count()
        return ApiResponse.ok(
            PageDto(
                items = jobs.map { ProviderMapper.toJobDto(it) },
                total = total,
                page = page,
                pageSize = pageSize,
                hasMore = page * pageSize < total,
            )
        )
    }

    @GetMapping("/jobs/{id}")
    fun jobDetail(@PathVariable id: String): ApiResponse<JobDto> {
        val provider = registry.active
        val job = provider.detail(id)
            ?: throw ApiException(ApiCode.NOT_FOUND, "职位不存在")
        return ApiResponse.ok(ProviderMapper.toJobDto(job))
    }

    @GetMapping("/companies/{name}")
    fun company(@PathVariable name: String): ApiResponse<CompanyDto?> {
        val provider = registry.active
        val company = provider.company(name)?.let {
            CompanyDto(id = 0L, name = it.name, city = it.city, industry = it.industry, size = it.size)
        }
        return ApiResponse.ok(company)
    }

    @GetMapping("/providers")
    fun providers(): ApiResponse<Map<String, Any>> =
        ApiResponse.ok(
            mapOf(
                "active" to registry.active.key,
                "available" to registry.all.map { it.key },
            )
        )
}
