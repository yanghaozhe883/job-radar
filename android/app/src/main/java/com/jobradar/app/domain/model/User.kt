package com.jobradar.app.domain.model

import kotlinx.serialization.Serializable

/** Authenticated user identity. Pure domain model. */
@Serializable
data class User(
    val id: Long,
    val phone: String? = null,
    val nickname: String? = null,
    val avatarUrl: String? = null,
)
