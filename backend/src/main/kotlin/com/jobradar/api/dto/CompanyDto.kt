package com.jobradar.api.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * Company data-transfer object. Field names serialize to snake_case to match
 * the Android client's `CompanyDto` (data/remote/dto/CompanyDto.kt).
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CompanyDto(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
    val industry: String? = null,
    val size: String? = null,
    val city: String? = null,
    val financingStage: String? = null,
    val description: String? = null,
)
