package com.jobradar.app.presentation.auth

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState
import com.jobradar.app.domain.model.User

/** MVI contract for the Login screen. */
interface LoginContract : MviContract<LoginContract.State, LoginContract.Event, LoginContract.Effect> {

    data class State(
        val phone: String = "",
        val code: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
    ) : MviState

    sealed interface Event : MviEvent {
        data class OnPhoneChange(val phone: String) : Event
        data class OnCodeChange(val code: String) : Event
        object SignIn : Event
        object SendCode : Event
    }

    sealed interface Effect : MviEffect {
        data class LoginSuccess(val user: User) : Effect
        data class Toast(val message: String) : Effect
    }
}
