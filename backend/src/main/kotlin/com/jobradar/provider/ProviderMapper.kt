package com.jobradar.provider

import com.jobradar.api.dto.CompanyDto
import com.jobradar.api.dto.JobDto

/**
 * Maps a [ProviderJob] (provider-layer read model) to the unified `JobDto` /
 * `CompanyDto` (API contract that every client shares). This keeps the whole
 * product on ONE domain model — a client never sees provider-specific types.
 */
object ProviderMapper {

    fun toJobDto(p: ProviderJob): JobDto {
        val company = p.companyName?.let { name ->
            CompanyDto(
                id = 0L, // provider-local; not a DB id
                name = name,
                logoUrl = p.companyLogoUrl,
                city = p.city,
                industry = null,
                size = null,
            )
        }
        return JobDto(
            id = p.id.toLongOrNull() ?: 0L,
            title = p.title,
            city = p.city,
            salaryMinK = p.salaryMinK,
            salaryMaxK = p.salaryMaxK,
            jobType = p.jobType,
            experience = p.experience,
            education = p.education,
            skills = p.skills,
            description = p.description,
            company = company,
            dataSource = p.source,
            publishedAt = null,
        )
    }
}
