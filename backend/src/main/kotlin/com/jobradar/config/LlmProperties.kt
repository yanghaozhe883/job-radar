package com.jobradar.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * AnythingLLM (local knowledge base) connection config.
 *
 * The API key must come from the environment (`ANYTHINGLLM_API_KEY`) — NEVER
 * from a committed config file. This keeps the secret out of the repo and out
 * of the Android app entirely (only the backend holds it).
 */
@ConfigurationProperties(prefix = "llm")
data class LlmProperties(
    /** AnythingLLM base URL, e.g. http://127.0.0.1:3001 */
    val baseUrl: String = "http://127.0.0.1:3001",

    /** AnythingLLM API key from env var. */
    val apiKey: String = "",

    /** Workspace slug the assistant answers from (created for this app). */
    val workspaceSlug: String = "job-radar",
)
