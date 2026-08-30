package com.jobradar.api.controller

import com.jobradar.ai.AiService
import com.jobradar.api.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** AI-assistant endpoints that proxy the local knowledge base to the app. */
@RestController
@RequestMapping("\${api.base-path:/api/v1}/ai")
class AiController(
    private val aiService: AiService,
) {

    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ApiResponse<AiService.AiReply> =
        ApiResponse.ok(aiService.chat(request.message, request.mode ?: "query", request.workspaceSlug ?: "job-radar"))

    data class ChatRequest(
        val message: String,
        val mode: String? = "query",
        val workspaceSlug: String? = "job-radar",
    )
}
