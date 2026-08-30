package com.jobradar.app.data.repository

import com.jobradar.app.core.common.AppError
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.data.remote.AiApiService
import com.jobradar.app.data.remote.dto.AiChatRequest
import com.jobradar.app.domain.repository.AiAnswer
import com.jobradar.app.domain.repository.AiRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Proxies AI questions to the backend, which holds the knowledge-base key. */
@Singleton
class AiRepositoryImpl @Inject constructor(
    private val aiApi: AiApiService,
) : AiRepository {

    override suspend fun ask(question: String, mode: String): AppResult<AiAnswer> = try {
        val response = aiApi.chat(AiChatRequest(message = question, mode = mode))
        val data = response.requireData()
        AppResult.Success(
            AiAnswer(answer = data.answer, sources = data.sources, model = data.model)
        )
    } catch (e: Exception) {
        AppResult.Failure(AppError.Network)
    }
}
