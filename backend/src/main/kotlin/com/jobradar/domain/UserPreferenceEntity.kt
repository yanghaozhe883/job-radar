package com.jobradar.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * A user's radar targeting profile (city, roles, skills, salary band...).
 * Stored as a JSON document keyed by userId — flexible and easy to evolve
 * without schema migrations, while still queryable by userId.
 */
@Entity
@Table(name = "user_preferences")
class UserPreferenceEntity(
    @Id
    @Column(name = "user_id")
    val userId: Long,

    /** JSON-encoded [com.jobradar.api.dto.PreferenceDto]. */
    @Column(columnDefinition = "TEXT")
    val preferenceJson: String,

    @Column(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
