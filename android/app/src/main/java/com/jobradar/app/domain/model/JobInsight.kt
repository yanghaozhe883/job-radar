package com.jobradar.app.domain.model

/** v0.3 · Insight — makes a job understandable. Pure domain model. */
data class JobInsight(
    val jobId: String = "",
    val responsibilities: List<String> = emptyList(),
    val coreSkills: List<String> = emptyList(),
    val riskPoints: List<String> = emptyList(),
    val growth: List<String> = emptyList(),
    val whyRecommended: List<String> = emptyList(),
    val match: MatchInsight = MatchInsight(),
    val model: String? = null,
    val generatedBy: String = "model",
)

data class MatchInsight(
    val skillMatch: Int = 0,
    val experienceMatch: Int = 0,
    val directionMatch: Int = 0,
    val skillReason: String = "",
    val experienceReason: String = "",
    val directionReason: String = "",
) {
    val overall: Int get() = ((skillMatch * 0.4 + experienceMatch * 0.3 + directionMatch * 0.3)).toInt().coerceIn(0, 100)
}
