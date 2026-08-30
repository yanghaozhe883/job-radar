package com.jobradar.app.domain.model

import kotlinx.serialization.Serializable

/** A single job posting. Pure domain model. */
@Serializable
data class Job(
    val id: Long,
    val title: String,
    val city: String,
    val salaryMinK: Int,
    val salaryMaxK: Int,
    val jobType: JobType,
    val experience: ExperienceLevel,
    val education: EducationLevel,
    val skills: List<String>,
    val description: String? = null,
    val company: Company? = null,
    val dataSource: String? = null,
    val publishedAt: Long? = null,
)

@Serializable
enum class JobType(val label: String) {
    FULL_TIME("全职"),
    PART_TIME("兼职"),
    INTERNSHIP("实习"),
    REMOTE("远程"),
    CONTRACT("外包");
}

@Serializable
enum class ExperienceLevel(val label: String) {
    FRESH("应届"),
    JUNIOR("1-3年"),
    MID("3-5年"),
    SENIOR("5-10年"),
    EXPERT("10年以上");
}

@Serializable
enum class EducationLevel(val label: String) {
    HIGH_SCHOOL("高中"),
    ASSOCIATE("大专"),
    BACHELOR("本科"),
    MASTER("硕士"),
    PHD("博士");
}

/** Display-ready salary string, e.g. "25-35K". */
fun Job.salaryLabel(): String = "${salaryMinK}-${salaryMaxK}K"
