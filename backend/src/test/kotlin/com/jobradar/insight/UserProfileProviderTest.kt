package com.jobradar.insight

import com.fasterxml.jackson.databind.ObjectMapper
import com.jobradar.domain.UserPreferenceEntity
import com.jobradar.repo.UserPreferenceRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional

/**
 * v0.3 grounding: Backend owns the user context. The profile is built ONLY from
 * the saved preference — never invented. Missing fields stay empty (the prompt
 * turns them into "未提供"), so the model can't assert facts the user never gave.
 */
class UserProfileProviderTest {

    private val repo: UserPreferenceRepository = mock(UserPreferenceRepository::class.java)
    private val provider = PreferenceUserProfileProvider(repo, ObjectMapper())

    @Test
    fun `no saved preference yields an empty (not fabricated) profile`() {
        `when`(repo.findByUserId(99L)).thenReturn(Optional.empty())
        val profile = provider.profileFor(99L)
        assertTrue(profile.skills.isEmpty())
        assertEquals(0, profile.yearsOfExperience)
        assertTrue(profile.targetRoles.isEmpty())
    }

    @Test
    fun `saved preference maps to an authoritative profile`() {
        val json = """{"city":"上海","target_roles":["AI 应用开发"],
            "skill_tags":["Kotlin","RAG"],"years_of_experience":3}"""
        `when`(repo.findByUserId(1L)).thenReturn(Optional.of(UserPreferenceEntity(1L, json)))
        val profile = provider.profileFor(1L)
        assertEquals(listOf("Kotlin", "RAG"), profile.skills)
        assertEquals(listOf("AI 应用开发"), profile.targetRoles)
        assertEquals(3, profile.yearsOfExperience)
        assertEquals("上海", profile.city)
    }

    @Test
    fun `corrupt preference json safely falls back to empty`() {
        `when`(repo.findByUserId(2L)).thenReturn(Optional.of(UserPreferenceEntity(2L, "{not json")))
        val profile = provider.profileFor(2L)
        assertTrue(profile.skills.isEmpty())
        assertEquals(0, profile.yearsOfExperience)
    }
}
