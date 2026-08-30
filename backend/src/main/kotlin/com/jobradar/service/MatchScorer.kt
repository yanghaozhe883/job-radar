package com.jobradar.service

import java.util.Locale

/**
 * Backend matching engine.
 *
 * Mirrors the Android client's `domain/usecase/ScoreJobUseCase` philosophy so
 * both sides agree on what "matched" means. It combines:
 *  - skill/title keyword overlap (0..55)
 *  - salary attractiveness (0..30)
 *  - a baseline so a surfaced job is never a meaningless 0 (0..15)
 *
 * The radar uses this so new-job hits carry a real, comparable score instead of
 * an opaque 0 for jobs that simply don't substring-match a fixed keyword list.
 */
object MatchScorer {

    fun score(
        title: String,
        skills: List<String>,
        salaryMinK: Int,
        salaryMaxK: Int,
        keywords: List<String>?,
        baseline: Int = 45,
        targetSalaryMinK: Int = 15,
        targetSalaryMaxK: Int = 45,
    ): Int {
        val skillScore = skillScore(title, skills, keywords)           // 0..55
        val salaryScore = salaryScore(salaryMinK, salaryMaxK, targetSalaryMinK, targetSalaryMaxK) // 0..30
        val base = baseline.coerceIn(0, 15)                            // 0..15
        return (skillScore + salaryScore + base).coerceIn(0, 100)
    }

    /** Keyword/substring overlap of title+skills against the targets. 0..55. */
    private fun skillScore(title: String, skills: List<String>, keywords: List<String>?): Int {
        if (keywords.isNullOrEmpty()) return 40 // neutral when no targets given
        val text = (title + " " + skills.joinToString(" ")).lowercase(Locale.getDefault())
        val hits = keywords.count { it.lowercase(Locale.getDefault()) in text }
        return ((hits.toDouble() / keywords.size) * 55).toInt().coerceIn(0, 55)
    }

    /** How the pay range fits the target band. 0..30. */
    private fun salaryScore(salaryMinK: Int, salaryMaxK: Int, targetMin: Int, targetMax: Int): Int {
        val overlaps = salaryMaxK >= targetMin && salaryMinK <= targetMax
        if (!overlaps) return 8
        val midpoint = (salaryMaxK + salaryMinK) / 2
        val target = (targetMax + targetMin) / 2
        return when {
            midpoint >= target -> 30
            midpoint >= target * 0.85 -> 24
            midpoint >= target * 0.7 -> 18
            else -> 12
        }
    }
}
