package com.jobradar.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.jobradar.api.ApiCode
import com.jobradar.api.ApiException
import com.jobradar.api.dto.PreferenceDto
import com.jobradar.domain.UserEntity
import com.jobradar.domain.UserJobEntity
import com.jobradar.domain.UserJobKey
import com.jobradar.domain.UserPreferenceEntity
import com.jobradar.repo.UserJobRepository
import com.jobradar.repo.UserPreferenceRepository
import com.jobradar.repo.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import java.util.Optional

class UserServiceTest {

    private val userRepo: UserRepository = mock()
    private val userJobRepo: UserJobRepository = mock()
    private val prefRepo: UserPreferenceRepository = mock()
    private val mapper = ObjectMapper().registerKotlinModule()

    private val service = UserService(userRepo, userJobRepo, prefRepo, mapper)

    @Test
    fun `sign in with existing phone returns the user without recreating`() {
        val existing = UserEntity(id = 1, phone = "13800138000", nickname = "求职者 8000")
        `when`(userRepo.findByPhone("13800138000")).thenReturn(Optional.of(existing))

        val dto = service.signIn("13800138000", "123456")

        assertEquals(1L, dto.id)
        assertEquals("13800138000", dto.phone)
        // should NOT have called save (already exists)
        verify(userRepo, never()).save(any())
    }

    @Test
    fun `sign in with new phone creates the user`() {
        `when`(userRepo.findByPhone("13900139000")).thenReturn(Optional.empty())
        `when`(userRepo.save(any(UserEntity::class.java))).thenAnswer { it.getArgument(0) }

        val dto = service.signIn("13900139000", "654321")

        assertEquals("13900139000", dto.phone)
    }

    @Test
    fun `sign in rejects invalid phone or code`() {
        assertThrows(ApiException::class.java) { service.signIn("123", "123456") }
        assertThrows(ApiException::class.java) { service.signIn("13800138000", "123") }
        verify(userRepo, never()).save(any())
    }

    @Test
    fun `setJobStatus persists a favorite and returns it`() {
        `when`(userJobRepo.findByUserAndJob(1L, 5L)).thenReturn(Optional.empty())
        val saved = UserJobEntity(id = UserJobKey(1L, 5L), status = "FAVORITE", updatedAt = 1)
        `when`(userJobRepo.save(any(UserJobEntity::class.java))).thenReturn(saved)
        `when`(userJobRepo.findByUserAndJob(1L, 5L)).thenReturn(Optional.of(saved))

        val captor = ArgumentCaptor.forClass(UserJobEntity::class.java)
        val dto = service.setJobStatus(1L, 5L, "favorite") // lower-case -> should normalize
        verify(userJobRepo).save(captor.capture())
        assertEquals("FAVORITE", captor.value.status)
        assertEquals("FAVORITE", dto.status)
    }

    @Test
    fun `setJobStatus rejects unknown status`() {
        val e = assertThrows(ApiException::class.java) { service.setJobStatus(1L, 5L, "NOPE") }
        assertEquals(ApiCode.BAD_REQUEST, e.code)
    }

    @Test
    fun `preference round trips via json`() {
        val pref = PreferenceDto(city = "杭州", targetRoles = listOf("算法工程师"), salaryMaxK = 55)
        // simulate existing stored
        val stored = UserPreferenceEntity(userId = 1L, preferenceJson = mapper.writeValueAsString(pref))
        `when`(prefRepo.findByUserId(1L)).thenReturn(Optional.of(stored))

        val loaded = service.getPreference(1L)
        assertEquals("杭州", loaded.city)
        assertEquals("算法工程师", loaded.targetRoles.first())
        assertEquals(55, loaded.salaryMaxK)
    }

    @Test
    fun `getPreference returns default when absent`() {
        `when`(prefRepo.findByUserId(1L)).thenReturn(Optional.empty())
        val loaded = service.getPreference(1L)
        assertEquals("上海", loaded.city)
        // Backend PreferenceDto defaults targetRoles to empty list.
        assertTrue(loaded.targetRoles.isEmpty())
    }
}
