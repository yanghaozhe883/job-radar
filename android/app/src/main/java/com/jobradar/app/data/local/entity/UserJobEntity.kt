package com.jobradar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_jobs")
data class UserJobEntity(
    @PrimaryKey val jobId: Long,
    val status: String, // SEEN / FAVORITE / APPLIED / HIDDEN
    val updatedAt: Long,
)
