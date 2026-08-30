package com.jobradar.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val phone: String,
    val code: String,
)

@Serializable
data class UserDto(
    val id: Long,
    val phone: String? = null,
    val nickname: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class PreferenceDto(
    val city: String = "上海",
    @SerialName("target_roles") val targetRoles: List<String> = emptyList(),
    @SerialName("skill_tags") val skillTags: List<String> = emptyList(),
    @SerialName("salary_min_k") val salaryMinK: Int = 20,
    @SerialName("salary_max_k") val salaryMaxK: Int = 45,
    @SerialName("preferred_job_types") val preferredJobTypes: List<String> = listOf("全职"),
    @SerialName("years_of_experience") val yearsOfExperience: Int = 3,
    @SerialName("preferred_companies") val preferredCompanies: List<String> = emptyList(),
)

@Serializable
data class UserJobRequest(
    @SerialName("job_id") val jobId: Long,
    val status: String,
)

@Serializable
data class UserJobDto(
    @SerialName("job_id") val jobId: Long,
    val status: String,
    val job: JobDto? = null,
    @SerialName("updated_at") val updatedAt: Long = 0,
)
