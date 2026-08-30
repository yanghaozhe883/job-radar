package com.jobradar.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.jobradar.api.ApiCode
import com.jobradar.api.ApiException
import com.jobradar.api.dto.JobDto
import com.jobradar.api.dto.PreferenceDto
import com.jobradar.api.dto.UserDto
import com.jobradar.api.dto.UserJobDto
import com.jobradar.domain.UserEntity
import com.jobradar.domain.UserJobEntity
import com.jobradar.domain.UserJobKey
import com.jobradar.domain.UserPreferenceEntity
import com.jobradar.repo.UserJobRepository
import com.jobradar.repo.UserPreferenceRepository
import com.jobradar.repo.UserRepository
import com.jobradar.service.JobMapper.toDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * User/session domain logic: login, preferences, and user<->job interactions.
 *
 * For the demo, "verification code" is accepted as any 6-digit string (no real
 * SMS). Swapping in a real SMS gateway later only touches [signIn].
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val userJobRepository: UserJobRepository,
    private val preferenceRepository: UserPreferenceRepository,
    private val mapper: ObjectMapper,
) {

    @Transactional
    fun signIn(phone: String, code: String): UserDto {
        val p = phone.trim()
        if (!Regex("^1\\d{10}$").matches(p) || code.length != 6) {
            throw ApiException(ApiCode.BAD_REQUEST, "手机号或验证码不正确")
        }
        // Get-or-create the user by phone.
        val user = userRepository.findByPhone(p).orElseGet {
            userRepository.save(UserEntity(phone = p, nickname = "求职者 ${p.takeLast(4)}"))
        }
        return user.toDto()
    }

    @Transactional(readOnly = true)
    fun getUser(userId: Long): UserDto =
        userRepository.findById(userId).map { it.toDto() }
            .orElseThrow { ApiException(ApiCode.NOT_FOUND, "用户不存在") }

    @Transactional
    fun setJobStatus(userId: Long, jobId: Long, status: String): UserJobDto {
        val normalized = status.uppercase()
        if (normalized !in SET_OF_STATUSES) {
            throw ApiException(ApiCode.BAD_REQUEST, "未知状态")
        }
        val existing = userJobRepository.findByUserAndJob(userId, jobId)
        val now = System.currentTimeMillis()
        userJobRepository.save(
            if (existing.isPresent) {
                val e = existing.get()
                UserJobEntity(id = e.id, status = normalized, job = e.job, updatedAt = now)
            } else {
                UserJobEntity(id = UserJobKey(userId = userId, jobId = jobId), status = normalized, updatedAt = now)
            }
        )
        // Re-fetch so the response also carries the joined job (eager FK).
        return userJobRepository.findByUserAndJob(userId, jobId).orElseThrow().toDto()
    }

    @Transactional(readOnly = true)
    fun listUserJobs(userId: Long, status: String?): List<UserJobDto> {
        val rows = if (status != null) {
            userJobRepository.findByUserAndStatus(userId, status.uppercase())
        } else {
            userJobRepository.findByUserOrderByUpdatedAtDesc(userId)
        }
        return rows.map { it.toDto() }
    }

    @Transactional
    fun savePreference(userId: Long, pref: PreferenceDto): PreferenceDto {
        val json = mapper.writeValueAsString(pref)
        preferenceRepository.findById(userId).ifPresent { preferenceRepository.delete(it) }
        preferenceRepository.save(UserPreferenceEntity(userId = userId, preferenceJson = json))
        return pref
    }

    @Transactional(readOnly = true)
    fun getPreference(userId: Long): PreferenceDto =
        preferenceRepository.findByUserId(userId)
            .map { runCatching { mapper.readValue(it.preferenceJson, PreferenceDto::class.java) }.getOrDefault(PreferenceDto()) }
            .orElse(PreferenceDto())

    // --- entity -> dto ---

    private fun UserEntity.toDto() = UserDto(id = id ?: 0L, phone = phone, nickname = nickname, avatarUrl = avatarUrl)

    private fun UserJobEntity.toDto(): UserJobDto {
        val jobDto: JobDto? = job?.let { it.toDto(it.matchScore) }
        return UserJobDto(jobId = jobId, status = status, job = jobDto, updatedAt = updatedAt)
    }

    private companion object {
        val SET_OF_STATUSES = setOf("SEEN", "FAVORITE", "APPLIED", "HIDDEN")
    }
}
