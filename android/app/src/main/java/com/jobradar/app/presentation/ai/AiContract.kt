package com.jobradar.app.presentation.ai

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState

/** MVI contract for the AI assistant screen (knowledge-base backed). */
interface AiContract : MviContract<AiContract.State, AiContract.Event, AiContract.Effect> {

    data class Message(
        val fromUser: Boolean,
        val text: String,
        val sources: List<String> = emptyList(),
    )

    data class State(
        val messages: List<Message> = emptyList(),
        val input: String = "",
        val isLoading: Boolean = false,
    ) : MviState

    sealed interface Event : MviEvent {
        object OnEnter : Event
        data class OnInputChange(val text: String) : Event
        object Send : Event
    }

    sealed interface Effect : MviEffect {
        data class Toast(val message: String) : Effect
    }
}
