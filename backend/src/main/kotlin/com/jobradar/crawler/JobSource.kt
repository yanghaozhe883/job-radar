package com.jobradar.crawler

/**
 * A normalized job as collected from a source, before mapping to a persistence
 * entity. This is the seam that keeps "how we fetch" independent from "how we
 * store", so sources can be swapped without touching the DB layer.
 */
data class RawJob(
    /** Stable external id from the source (for de-duplication). */
    val externalId: String,
    val title: String,
    val city: String,
    val salaryMinK: Int,
    val salaryMaxK: Int,
    val jobType: String = "全职",
    val experience: String = "1-3年",
    val education: String = "本科",
    val skills: List<String> = emptyList(),
    val description: String? = null,
    val companyName: String? = null,
    val companyLogoUrl: String? = null,
    val source: String,
    val publishedAt: Long = System.currentTimeMillis(),
)

/**
 * A pluggable source of job postings. Compliance rules live here:
 * every implementation must only read public data, respect robots.txt, rate
 * limit, and identify itself. Never access login state or personal data.
 */
interface JobSource {
    /** A short, unique key for the source (used for provenance in `dataSource`). */
    val key: String

    /** Fetch a batch of normalized jobs. Must already be de-duplicated-ish. */
    fun fetch(): List<RawJob>
}
