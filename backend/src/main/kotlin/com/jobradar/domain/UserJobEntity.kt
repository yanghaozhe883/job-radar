package com.jobradar.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.io.Serializable

/**
 * Composite key for [UserJobEntity]: (userId, jobId).
 * Top-level + @Embeddable so the kotlin-jpa plugin adds the no-arg constructor
 * Hibernate needs to instantiate it reflectively.
 */
@Embeddable
data class UserJobKey(
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(name = "job_id")
    val jobId: Long = 0,
) : Serializable

/**
 * A user's interaction with a job (favorite / applied / seen / hidden).
 * The single source of truth for "my jobs".
 */
@Entity
@Table(name = "user_jobs")
class UserJobEntity(
    @Id
    val id: UserJobKey,

    /** SEEN / FAVORITE / APPLIED / HIDDEN (mirrors the Android JobStatus enum). */
    val status: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", insertable = false, updatable = false)
    val job: JobEntity? = null,

    @Column(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
) {
    val userId: Long get() = id.userId
    val jobId: Long get() = id.jobId
}
