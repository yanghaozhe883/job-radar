package com.jobradar.app.presentation.resume

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState
import com.jobradar.app.domain.model.Resume

/** MVI contract for the resume screen. */
interface ResumeContract : MviContract<ResumeContract.State, ResumeContract.Event, ResumeContract.Effect> {

    data class State(
        val resume: Resume? = null,
        val isLoading: Boolean = true,
    ) : MviState

    sealed interface Event : MviEvent {
        object OnEnter : Event
    }

    sealed interface Effect : MviEffect
}
