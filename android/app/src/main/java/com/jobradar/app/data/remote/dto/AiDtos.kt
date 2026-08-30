package com.jobradar.app.data.remote.dto

import kotlinx.serialization.Serializable

/** Request to the backend AI assistant (which proxies the local knowledge base). */
@Serializable
data class AiChatRequest(
    val message: String,
    val mode: String = "query",
    val workspaceSlug: String = "job-radar",
)

/** Response from the backend AI assistant. */
@Serializable
data class AiChatResponse(
    val answer: String = "",
    val sources: List<String> = emptyList(),
    val model: String? = null,
)
