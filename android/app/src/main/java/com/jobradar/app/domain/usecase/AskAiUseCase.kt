package com.jobradar.app.domain.usecase

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.repository.AiAnswer
import com.jobradar.app.domain.repository.AiRepository
import javax.inject.Inject

/** Ask the knowledge-base AI assistant a question. */
class AskAiUseCase @Inject constructor(
    private val repository: AiRepository,
) {
    suspend operator fun invoke(question: String, mode: String = "query"): AppResult<AiAnswer> =
        repository.ask(question, mode)
}
