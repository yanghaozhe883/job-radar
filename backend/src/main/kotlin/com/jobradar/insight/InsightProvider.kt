package com.jobradar.insight

import com.jobradar.domain.JobInsight

/** The user's profile used to judge a job's fit — the "画像" side of Insight. */
data class UserProfile(
    /** Target role keywords, e.g. ["AI 应用开发", "智能体工程"]. */
    val targetRoles: List<String> = emptyList(),
    /** Skills the user has, e.g. ["Kotlin", "RAG", "Python"]. */
    val skills: List<String> = emptyList(),
    /** Years of experience. */
    val yearsOfExperience: Int = 0,
    /** Target city (optional). */
    val city: String? = null,
)

/**
 * InsightProvider is THE seam that turns a Job + UserProfile into a JobInsight.
 *
 * v0.3 ships [AnythingLlmInsightProvider] (reuses AiService). Later this can be
 * swapped for Ollama-native JSON, 王庭's Agent orchestration, or a third-party
 * model — WITHOUT touching the product/business layer. That is the "provider
 * swap instead of rewrite" discipline we keep.
 */
interface InsightProvider {
    /** Compute a [JobInsight] for a job against the user profile. */
    fun generate(job: InsightJob, profile: UserProfile): JobInsight
}

/** The minimal Job view the Insight needs (decoupled from the persistence entity). */
data class InsightJob(
    val id: String,
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
)
