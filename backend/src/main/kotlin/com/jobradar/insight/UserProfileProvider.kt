package com.jobradar.insight

import com.fasterxml.jackson.databind.ObjectMapper
import com.jobradar.api.dto.PreferenceDto
import com.jobradar.repo.UserPreferenceRepository
import org.springframework.stereotype.Component

/**
 * UserProfileProvider — the authoritative source of the user context.
 *
 * Core v0.3 grounding rule: **Client chooses the job, Backend owns the user
 * context.** Insight is computed against THIS profile (from the saved user
 * preference), NOT from client-supplied query params. Missing info stays empty
 * (never fabricated), so the model can't invent facts the user never stated.
 */
interface UserProfileProvider {
    /** Build the [UserProfile] for a user, or a minimal/empty profile if none. */
    fun profileFor(userId: Long?): UserProfile
}

/**
 * Reads the saved radar preference (city / targetRoles / skillTags / years).
 * Falls back to an empty profile when the user hasn't configured one — missing
 * fields are LEFT EMPTY (and the prompt turns them into "未提供"), never guessed.
 */
@Component
class PreferenceUserProfileProvider(
    private val preferenceRepository: UserPreferenceRepository,
    private val mapper: ObjectMapper,
) : UserProfileProvider {

    override fun profileFor(userId: Long?): UserProfile {
        if (userId == null) return UserProfile()
        return preferenceRepository.findByUserId(userId)
            .map { parse(it.preferenceJson) }
            .orElse(UserProfile())
    }

    private fun parse(json: String): UserProfile = runCatching {
        val pref = mapper.readValue(json, PreferenceDto::class.java)
        UserProfile(
            targetRoles = pref.targetRoles,
            skills = pref.skillTags,
            yearsOfExperience = if (pref.yearsOfExperience > 0) pref.yearsOfExperience else 0,
            city = pref.city,
        )
    }.getOrDefault(UserProfile())
}
