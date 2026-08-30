package com.jobradar.app.domain.model

import kotlinx.serialization.Serializable

/** Filter applied to the jobs stream. Defaults fully open. */
@Serializable
data class JobFilter(
    val city: String? = null,
    val keyword: String? = null,
    val jobTypes: List<String> = emptyList(),
    val minSalaryK: Int? = null,
    val sort: Sort = Sort.COMPREHENSIVE,
)

@Serializable
enum class Sort(val label: String) {
    COMPREHENSIVE("综合"),
    LATEST("最新"),
    SALARY("薪资"),
    MATCH("匹配");
}

/** Interaction status of a user<->job relationship. */
enum class JobStatus {
    SEEN,
    FAVORITE,
    APPLIED,
    HIDDEN,
}
