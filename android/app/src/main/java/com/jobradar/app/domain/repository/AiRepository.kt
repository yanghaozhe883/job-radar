package com.jobradar.app.domain.repository

import com.jobradar.app.core.common.AppResult

/** Ask the knowledge-base-backed AI assistant a question. */
interface AiRepository {
    suspend fun ask(question: String, mode: String = "query"): AppResult<AiAnswer>
}

/** A single AI answer from the knowledge base. */
data class AiAnswer(
    val answer: String,
    val sources: List<String> = emptyList(),
    val model: String? = null,
)
