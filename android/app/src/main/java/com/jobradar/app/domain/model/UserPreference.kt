package com.jobradar.app.domain.model

import kotlinx.serialization.Serializable

/**
 * The user's targeting profile — what the "radar" is scanning for.
 * Persisted via DataStore as JSON; drives the matching engine.
 */
@Serializable
data class UserPreference(
    val city: String = "上海",
    val targetRoles: List<String> = listOf("前端工程师", "Android 工程师"),
    val skillTags: List<String> = emptyList(),
    val salaryMinK: Int = 20,
    val salaryMaxK: Int = 45,
    val preferredJobTypes: List<String> = listOf("全职"),
    val yearsOfExperience: Int = 3,
    val preferredCompanies: List<String> = emptyList(),
) {
    companion object {
        val DEFAULT = UserPreference()
    }
}

/** Weighted score from the matching engine, with a human-readable reason. */
@Serializable
data class MatchScore(
    val total: Int,
    val salaryMatch: Int,
    val skillMatch: Int,
    val companyMatch: Int,
    val reason: String,
)
