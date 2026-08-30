package com.jobradar.app.domain.model

import kotlinx.serialization.Serializable

/** Company info attached to a job posting. Pure domain model. */
@Serializable
data class Company(
    val id: Long,
    val name: String,
    val logoUrl: String? = null,
    val industry: String? = null,
    val size: String? = null,
    val city: String? = null,
    val financingStage: String? = null,
    val description: String? = null,
)
