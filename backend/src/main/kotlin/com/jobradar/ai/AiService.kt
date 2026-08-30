package com.jobradar.ai

import com.jobradar.api.ApiCode
import com.jobradar.api.ApiException
import com.jobradar.config.LlmProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

/**
 * Proxies to the local AnythingLLM knowledge base.
 *
 * The API key is held only here (from [LlmProperties.apiKey]); the Android app
 * never sees it — it only talks to this backend's `/api/v1/ai/chat`. This keeps
 * the secret out of the client and out of the git history.
 */
@Service
class AiService(
    private val props: LlmProperties,
    private val mapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(AiService::class.java)
    private val client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .version(HttpClient.Version.HTTP_1_1)
        .build()

    data class AiReply(
        val answer: String,
        val sources: List<String> = emptyList(),
        val model: String? = null,
    )

    /** Ask the knowledge base. `mode=query` = RAG retrieval; `chat` = full conversation. */
    fun chat(message: String, mode: String = "query", workspaceSlug: String = props.workspaceSlug): AiReply {
        if (props.apiKey.isBlank()) {
            throw ApiException(ApiCode.INTERNAL, "LLM API key 未配置（设置 ANYTHINGLLM_API_KEY）")
        }
        // Build the JSON by hand so the exact bytes match what AnythingLLM expects
        // (avoids any Jackson naming/reordering surprises with the raw map body).
        val body = """{"message":${mapper.writeValueAsString(message)},"mode":${mapper.writeValueAsString(mode)}}"""
        val chatUrl = "${props.baseUrl.trimEnd('/')}/api/v1/workspace/$workspaceSlug/chat"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(chatUrl))
            .header("Authorization", "Bearer ${props.apiKey}")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "JobRadarBackend/1.0")
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            log.warn("AnythingLLM returned {}: {}", response.statusCode(), response.body().take(300))
            throw ApiException(ApiCode.INTERNAL, "知识库服务异常（${response.statusCode()}）: ${response.body().take(200)}")
        }
        return parse(response.body())
    }

    private fun parse(json: String): AiReply {
        val node: JsonNode = mapper.readTree(json)
        val answer = node.get("textResponse")?.asText() ?: run {
            // Streaming responses may arrive as one event; grab the content field.
            node.get("content")?.asText() ?: "（无回答）"
        }
        val sources = node.get("sources")?.mapNotNull { it.asText() } ?: emptyList()
        val model = node.get("metrics")?.get("model")?.asText()
        return AiReply(answer = answer, sources = sources, model = model)
    }
}
