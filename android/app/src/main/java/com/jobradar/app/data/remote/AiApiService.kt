package com.jobradar.app.data.remote

import com.jobradar.app.data.remote.dto.AiChatRequest
import com.jobradar.app.data.remote.dto.AiChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

/** Calls the backend's AI assistant, which proxies the local knowledge base. */
interface AiApiService {

    @POST("/api/v1/ai/chat")
    suspend fun chat(@Body request: AiChatRequest): ApiResponse<AiChatResponse>
}
