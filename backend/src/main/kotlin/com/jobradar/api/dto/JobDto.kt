package com.jobradar.api.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * Job data-transfer object. snake_case keys match the client's `JobDto`.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class JobDto(
    val id: Long,
    val title: String,
    val city: String,
    val salaryMinK: Int,
    val salaryMaxK: Int,
    val jobType: String? = "全职",
    val experience: String? = "1-3年",
    val education: String? = "本科",
    val skills: List<String> = emptyList(),
    val description: String? = null,
    val company: CompanyDto? = null,
    val dataSource: String? = null,
    val publishedAt: Long? = null,
    val matchScore: Int = 0,
)

/** Paginated list payload (mirrors the client's PageDto). */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PageDto<T>(
    val items: List<T> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
    val hasMore: Boolean = false,
)

/** Sort option parsed from the `sort` query param. */
enum class SortOption(val label: String) {
    COMPREHENSIVE("综合"),
    LATEST("最新"),
    SALARY("薪资"),
    MATCH("匹配");

    companion object {
        fun from(raw: String?): SortOption =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: COMPREHENSIVE
    }
}
