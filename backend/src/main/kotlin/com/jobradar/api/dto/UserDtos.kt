package com.jobradar.api.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/** Auth request: phone + verification code. */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AuthRequest(
    val phone: String,
    val code: String,
)

/** User profile returned by auth / user endpoints. */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserDto(
    val id: Long,
    val phone: String? = null,
    val nickname: String? = null,
    val avatarUrl: String? = null,
)

/** The user's radar targeting profile (mirrors the client's UserPreference). */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PreferenceDto(
    val city: String = "上海",
    val targetRoles: List<String> = emptyList(),
    val skillTags: List<String> = emptyList(),
    val salaryMinK: Int = 20,
    val salaryMaxK: Int = 45,
    val preferredJobTypes: List<String> = listOf("全职"),
    val yearsOfExperience: Int = 3,
    val preferredCompanies: List<String> = emptyList(),
)

/** A user's interaction with a job: { jobId, status }. */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserJobRequest(
    val jobId: Long,
    val status: String,
)

/** Enriched user-job row for "my jobs" (favorites / applied). */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserJobDto(
    val jobId: Long,
    val status: String,
    val job: JobDto? = null,
    val updatedAt: Long = 0,
)
