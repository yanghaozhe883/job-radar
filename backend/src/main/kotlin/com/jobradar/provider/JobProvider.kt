package com.jobradar.provider

/**
 * A normalized job as returned by a [JobProvider] — the product-facing read
 * model. Independent of persistence so a provider can serve data straight from
 * Mock / CSV / an external API without touching the DB.
 */
data class ProviderJob(
    val id: String,
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
)

/** Normalized company from a provider. */
data class ProviderCompany(
    val name: String,
    val city: String? = null,
    val industry: String? = null,
    val size: String? = null,
    val description: String? = null,
)

/** Filter for searching opportunities. */
data class JobSearchFilter(
    val city: String? = null,
    val keyword: String? = null,
    val jobType: String? = null,
    val minSalaryK: Int? = null,
    val page: Int = 1,
    val pageSize: Int = 20,
)

/**
 * Pluggable data source for the product. This is THE seam that lets JobRadar
 * swap data sources without touching UI or business logic. Compliance boundary:
 * implementations only read public data, respect rate limits, and identify
 * themselves. No scraping of login-protected or personal data.
 *
 * v0.1 ships [MockJobProvider]; CSV / API / third-party adapters are future
 * plugins added by implementing this interface.
 */
interface JobProvider {
    /** Unique key, e.g. "mock", "csv", "boss" (future). */
    val key: String

    /** Search opportunities. */
    fun search(filter: JobSearchFilter): List<ProviderJob>

    /** Fetch a single job by its provider-local id. */
    fun detail(id: String): ProviderJob?

    /** Fetch company info by name (optional; may return null). */
    fun company(name: String): ProviderCompany?

    /** Total available records (for paging / counts). */
    fun count(): Int
}
