package com.jobradar.app.presentation.profile

import androidx.lifecycle.viewModelScope
import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.usecase.ObservePreferencesUseCase
import com.jobradar.app.domain.usecase.SignOutUseCase
import com.jobradar.app.domain.usecase.UpdatePreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observePreferences: ObservePreferencesUseCase,
    private val updatePreference: UpdatePreferenceUseCase,
    private val signOut: SignOutUseCase,
) : MviViewModel<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect>(ProfileContract.State()) {

    init {
        observePreferences()
            .onEach { pref -> reduce { it.copy(preference = pref) } }
            .launchIn(viewModelScope)
    }

    override suspend fun handleEvent(event: ProfileContract.Event) {
        when (event) {
            is ProfileContract.Event.OnEnter -> Unit
            is ProfileContract.Event.OnCityChange ->
                reduce { it.copy(preference = it.preference.copy(city = event.city)) }
            is ProfileContract.Event.OnRoleToggle -> {
                val current = state.value.preference
                val toggled = if (event.role in current.targetRoles) {
                    current.targetRoles - event.role
                } else {
                    current.targetRoles + event.role
                }
                reduce { it.copy(preference = current.copy(targetRoles = toggled)) }
            }
            is ProfileContract.Event.OnSavePreference -> save(event.preference)
            is ProfileContract.Event.SignOut -> {
                signOut()
                emitEffect(ProfileContract.Effect.SignedOut)
            }
        }
    }

    private fun save(preference: UserPreference) {
        viewModelScope.launch {
            reduce { it.copy(isSaving = true) }
            val result = updatePreference(preference)
            reduce { it.copy(isSaving = false, preference = preference) }
            emitEffect(ProfileContract.Effect.Toast(if (result.isSuccess()) "雷达已更新" else "保存失败"))
        }
    }
}
