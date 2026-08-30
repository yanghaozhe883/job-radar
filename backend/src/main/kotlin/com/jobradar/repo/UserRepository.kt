package com.jobradar.repo

import com.jobradar.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByPhone(phone: String): Optional<UserEntity>
}
