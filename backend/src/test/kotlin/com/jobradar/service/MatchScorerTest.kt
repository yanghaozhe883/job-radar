package com.jobradar.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MatchScorerTest {

    @Test
    fun `high keyword overlap scores high`() {
        val score = MatchScorer.score(
            title = "Android 工程师",
            skills = listOf("Kotlin", "Jetpack Compose"),
            salaryMinK = 25,
            salaryMaxK = 40,
            keywords = listOf("android", "kotlin"),
            baseline = 5, // neutral floor
        )
        // 2/2 keyword hits -> full 55 skill + strong salary + floor
        assertTrue(score >= 60, "expected high score, got $score")
    }

    @Test
    fun `no keywords falls back to a neutral skill score`() {
        val score = MatchScorer.score(
            title = "任何岗位",
            skills = emptyList(),
            salaryMinK = 20,
            salaryMaxK = 40,
            keywords = null,
            baseline = 5,
        )
        // null keywords -> skillScore = 40 (neutral)
        assertTrue(score >= 40, "expected neutral baseline, got $score")
    }

    @Test
    fun `salary outside band scores lower than inside`() {
        val inside = MatchScorer.score("x", emptyList(), 25, 40, listOf("x"), baseline = 0)
        val outside = MatchScorer.score("x", emptyList(), 120, 200, listOf("x"), baseline = 0)
        assertTrue(inside > outside, "inside=$inside outside=$outside")
    }

    @Test
    fun `score always clamps to zero to one hundred`() {
        val score = MatchScorer.score(
            title = "X",
            skills = listOf("a", "b", "c", "d"),
            salaryMinK = 15,
            salaryMaxK = 45,
            keywords = listOf("a", "b", "c", "d"),
            baseline = 15,
        )
        assertTrue(score in 0..100)
    }
}
