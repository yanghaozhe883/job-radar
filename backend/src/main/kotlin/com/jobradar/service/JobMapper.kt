package com.jobradar.service

import com.jobradar.api.dto.CompanyDto
import com.jobradar.api.dto.JobDto
import com.jobradar.domain.CompanyEntity
import com.jobradar.domain.JobEntity

/** Entity <-> DTO mappers. Kept in the service layer for a single mapping seam. */
object JobMapper {

    fun CompanyEntity.toDto(): CompanyDto = CompanyDto(
        id = id ?: 0L,
        name = name,
        logoUrl = logoUrl,
        industry = industry,
        size = size,
        city = city,
        financingStage = financingStage,
        description = description,
    )

    fun JobEntity.toDto(score: Int = matchScore): JobDto = JobDto(
        id = id ?: 0L,
        title = title,
        city = city,
        salaryMinK = salaryMinK,
        salaryMaxK = salaryMaxK,
        jobType = jobType,
        experience = experience,
        education = education,
        skills = skills.toList(),
        description = description,
        company = company?.toDto(),
        dataSource = dataSource,
        publishedAt = publishedAt,
        matchScore = score,
    )
}
