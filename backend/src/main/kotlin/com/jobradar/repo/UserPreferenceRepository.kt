package com.jobradar.repo

import com.jobradar.domain.UserPreferenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserPreferenceRepository : JpaRepository<UserPreferenceEntity, Long> {
    fun findByUserId(userId: Long): Optional<UserPreferenceEntity>
}
