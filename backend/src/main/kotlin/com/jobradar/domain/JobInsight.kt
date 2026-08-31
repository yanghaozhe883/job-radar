package com.jobradar.domain

/**
 * JobInsight — the domain model that makes a job "understandable".
 *
 * This is v0.3 · Insight's core: it answers "what does this job actually mean
 * for THIS user?" with 6 explainable fields, so the user (and an interviewer,
 * or a future Agent) can trust the judgment — not an opaque single number.
 *
 * IMPORTANT: `match` is EXPLAINABLE, not a model hallucinated percentage.
 * It is decomposed into per-dimension scores (skills / experience / direction),
 * each with a short reason, then summarized into an overall level.
 */
data class JobInsight(
    val jobId: String,
    val responsibilities: List<String> = emptyList(),
    val coreSkills: List<String> = emptyList(),
    val riskPoints: List<String> = emptyList(),
    val growth: List<String> = emptyList(),
    /** Why this job is recommended for THIS user. */
    val whyRecommended: List<String> = emptyList(),
    val match: MatchInsight = MatchInsight(),
    val model: String? = null,
    /** Whether this came from a real model or a degraded/fallback path. */
    val generatedBy: String = "model",   // "model" | "fallback"
)

/**
 * Explainable match. NEVER a single bare percentage — it's decomposed:
 * - skillMatch     : how well the job's skills overlap the user's
 * - experienceMatch: how well the user's experience fits the job's requirement
 * - directionMatch : how well the job's direction aligns with the user's target
 * Overall level is derived from these, not invented.
 */
data class MatchInsight(
    val skillMatch: Int = 0,        // 0..100
    val experienceMatch: Int = 0,   // 0..100
    val directionMatch: Int = 0,    // 0..100
    val skillReason: String = "",
    val experienceReason: String = "",
    val directionReason: String = "",
) {
    /** Weighted overall level (0..100) — derived, transparent, not hallucinated. */
    val overall: Int
        get() = ((skillMatch * 0.4 + experienceMatch * 0.3 + directionMatch * 0.3)).toInt().coerceIn(0, 100)
}
