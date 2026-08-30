package com.jobradar.app.data.mapper

import com.jobradar.app.data.local.entity.JobEntity
import com.jobradar.app.data.local.entity.JobWithStatusEntity
import com.jobradar.app.data.remote.dto.CompanyDto
import com.jobradar.app.data.remote.dto.JobDto
import com.jobradar.app.domain.model.Company
import com.jobradar.app.domain.model.EducationLevel
import com.jobradar.app.domain.model.ExperienceLevel
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobStatus
import com.jobradar.app.domain.model.JobType

/**
 * Bidirectional mappings between the data-layer representations (DTO / Room
 * entity) and the pure domain model. Keeping these here means the domain layer
 * stays 100% framework-free and the mappings are unit-testable.
 */

private fun String.toJobType() = runCatching { JobType.valueOf(this) }.getOrElse {
    when {
        this.contains("远程") -> JobType.REMOTE
        this.contains("实习") -> JobType.INTERNSHIP
        this.contains("兼职") -> JobType.PART_TIME
        this.contains("外包") -> JobType.CONTRACT
        else -> JobType.FULL_TIME
    }
}

private fun String.toExperience() = runCatching { ExperienceLevel.valueOf(this) }.getOrElse {
    when {
        this.contains("应届") -> ExperienceLevel.FRESH
        this.contains("10年以上") -> ExperienceLevel.EXPERT
        this.contains("5-10") || this.contains("5年") -> ExperienceLevel.SENIOR
        this.contains("3-5") || this.contains("3年") -> ExperienceLevel.MID
        else -> ExperienceLevel.JUNIOR
    }
}

private fun String.toEducation() = runCatching { EducationLevel.valueOf(this) }.getOrElse {
    when {
        this.contains("博士") -> EducationLevel.PHD
        this.contains("硕士") -> EducationLevel.MASTER
        this.contains("高中") -> EducationLevel.HIGH_SCHOOL
        this.contains("大专") -> EducationLevel.ASSOCIATE
        else -> EducationLevel.BACHELOR
    }
}

private fun JobStatus.nameOrUnknown(): String = this.name

// --- DTO -> Domain ---

private fun CompanyDto.toDomain() = Company(
    id = id,
    name = name,
    logoUrl = logoUrl,
    industry = industry,
    size = size,
    city = city,
    financingStage = financingStage,
    description = description,
)

fun JobDto.toDomain() = Job(
    id = id,
    title = title,
    city = city,
    salaryMinK = salaryMinK,
    salaryMaxK = salaryMaxK,
    jobType = jobType.orEmpty().toJobType(),
    experience = experience.orEmpty().toExperience(),
    education = education.orEmpty().toEducation(),
    skills = skills,
    description = description,
    company = company?.toDomain(),
    dataSource = dataSource,
    publishedAt = publishedAt,
)

// --- Room entity -> Domain ---

fun JobEntity.toDomain() = Job(
    id = id,
    title = title,
    city = city,
    salaryMinK = salaryMinK,
    salaryMaxK = salaryMaxK,
    jobType = jobType.toJobType(),
    experience = experience.toExperience(),
    education = education.toEducation(),
    skills = skills,
    description = description,
    company = if (companyName != null) Company(
        id = id,
        name = companyName,
        logoUrl = companyLogoUrl,
        industry = companyIndustry,
        description = companyDescription,
    ) else null,
    dataSource = dataSource,
    publishedAt = publishedAt,
)

fun JobWithStatusEntity.toDomain() = Job(
    id = id,
    title = title,
    city = city,
    salaryMinK = salaryMinK,
    salaryMaxK = salaryMaxK,
    jobType = jobType.toJobType(),
    experience = experience.toExperience(),
    education = education.toEducation(),
    skills = skills,
    description = description,
    company = if (companyName != null) Company(
        id = id,
        name = companyName,
        logoUrl = companyLogoUrl,
        industry = companyIndustry,
        description = companyDescription,
    ) else null,
    dataSource = dataSource,
    publishedAt = publishedAt,
)

fun Job.toEntity(matchScore: Int = 0, cachedAt: Long = System.currentTimeMillis()) = JobEntity(
    id = id,
    title = title,
    city = city,
    salaryMinK = salaryMinK,
    salaryMaxK = salaryMaxK,
    jobType = jobType.name,
    experience = experience.name,
    education = education.name,
    skills = skills,
    description = description,
    companyName = company?.name,
    companyLogoUrl = company?.logoUrl,
    companyIndustry = company?.industry,
    companyDescription = company?.description,
    dataSource = dataSource,
    publishedAt = publishedAt,
    matchScore = matchScore,
    cachedAt = cachedAt,
)
