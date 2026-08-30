package com.jobradar.repo

import com.jobradar.domain.UserJobEntity
import com.jobradar.domain.UserJobKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface UserJobRepository : JpaRepository<UserJobEntity, UserJobKey> {

    @Query("SELECT u FROM UserJobEntity u WHERE u.id.userId = :userId AND u.status = :status")
    fun findByUserAndStatus(@Param("userId") userId: Long, @Param("status") status: String): List<UserJobEntity>

    @Query("SELECT u FROM UserJobEntity u WHERE u.id.userId = :userId ORDER BY u.updatedAt DESC")
    fun findByUserOrderByUpdatedAtDesc(@Param("userId") userId: Long): List<UserJobEntity>

    @Query("SELECT u FROM UserJobEntity u WHERE u.id.userId = :userId AND u.id.jobId = :jobId")
    fun findByUserAndJob(@Param("userId") userId: Long, @Param("jobId") jobId: Long): Optional<UserJobEntity>
}
