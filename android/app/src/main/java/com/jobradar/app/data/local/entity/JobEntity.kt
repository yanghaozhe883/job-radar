package com.jobradar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val city: String,
    val salaryMinK: Int,
    val salaryMaxK: Int,
    val jobType: String,
    val experience: String,
    val education: String,
    val skills: List<String>,
    val description: String?,
    val companyName: String?,
    val companyLogoUrl: String?,
    val companyIndustry: String?,
    val companyDescription: String?,
    val dataSource: String?,
    val publishedAt: Long?,
    val matchScore: Int,
    val cachedAt: Long,
)

/** Joined read model for the feed. */
data class JobWithStatusEntity(
    val id: Long,
    val title: String,
    val city: String,
    val salaryMinK: Int,
    val salaryMaxK: Int,
    val jobType: String,
    val experience: String,
    val education: String,
    val skills: List<String>,
    val description: String?,
    val companyName: String?,
    val companyLogoUrl: String?,
    val companyIndustry: String?,
    val companyDescription: String?,
    val dataSource: String?,
    val publishedAt: Long?,
    val matchScore: Int,
    val cachedAt: Long,
    val status: String?,
)