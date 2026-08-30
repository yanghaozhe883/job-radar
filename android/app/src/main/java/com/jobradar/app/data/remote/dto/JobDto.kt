package com.jobradar.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: Long,
    val name: String,
    @SerialName("logo_url") val logoUrl: String? = null,
    val industry: String? = null,
    val size: String? = null,
    val city: String? = null,
    @SerialName("financing_stage") val financingStage: String? = null,
    val description: String? = null,
)

@Serializable
data class JobDto(
    val id: Long,
    val title: String,
    val city: String,
    @SerialName("salary_min_k") val salaryMinK: Int,
    @SerialName("salary_max_k") val salaryMaxK: Int,
    @SerialName("job_type") val jobType: String? = "全职",
    val experience: String? = "1-3年",
    val education: String? = "本科",
    val skills: List<String> = emptyList(),
    val description: String? = null,
    val company: CompanyDto? = null,
    @SerialName("data_source") val dataSource: String? = null,
    @SerialName("published_at") val publishedAt: Long? = null,
)
