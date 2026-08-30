package com.jobradar.app.presentation.profile

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState
import com.jobradar.app.domain.model.UserPreference

/** MVI contract for the Profile / preferences tab (Tab4). */
interface ProfileContract : MviContract<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect> {

    data class State(
        val preference: UserPreference = UserPreference.DEFAULT,
        val isSaving: Boolean = false,
    ) : MviState

    sealed interface Event : MviEvent {
        object OnEnter : Event
        data class OnRoleToggle(val role: String) : Event
        data class OnCityChange(val city: String) : Event
        data class OnSavePreference(val preference: UserPreference) : Event
        object SignOut : Event
    }

    sealed interface Effect : MviEffect {
        data class Toast(val message: String) : Effect
        object SignedOut : Effect
    }
}
