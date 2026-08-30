package com.jobradar.app.presentation.ai

import androidx.lifecycle.viewModelScope
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.usecase.AskAiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val askAi: AskAiUseCase,
) : MviViewModel<AiContract.State, AiContract.Event, AiContract.Effect>(AiContract.State()) {

    private val conversation = mutableListOf<AiContract.Message>()

    override suspend fun handleEvent(event: AiContract.Event) {
        when (event) {
            is AiContract.Event.OnEnter -> Unit
            is AiContract.Event.OnInputChange -> reduce { it.copy(input = event.text) }
            is AiContract.Event.Send -> send()
        }
    }

    private fun send() {
        val q = state.value.input.trim()
        if (q.isEmpty() || state.value.isLoading) return
        reduce { it.copy(input = "", isLoading = true) }
        conversation += AiContract.Message(fromUser = true, text = q)
        syncMessages()
        viewModelScope.launch {
            when (val result = askAi(q)) {
                is AppResult.Success -> {
                    conversation += AiContract.Message(
                        fromUser = false,
                        text = result.data.answer.ifBlank { "（知识库暂未返回内容）" },
                        sources = result.data.sources,
                    )
                    reduce { it.copy(isLoading = false) }
                    syncMessages()
                }
                is AppResult.Failure -> {
                    reduce { it.copy(isLoading = false) }
                    emitEffect(AiContract.Effect.Toast("AI 服务暂不可用，请稍后再试"))
                }
            }
        }
    }

    private fun syncMessages() = reduce { it.copy(messages = conversation.toList()) }
}
