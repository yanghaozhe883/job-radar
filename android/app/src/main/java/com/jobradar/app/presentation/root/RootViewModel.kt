package com.jobradar.app.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobradar.app.domain.model.User
import com.jobradar.app.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Holds the top-level auth gate state. Exposes the signed-in user (null when
 * logged out) as a [StateFlow] that drives the login/main switch.
 */
@HiltViewModel
class RootViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
) : ViewModel() {

    val session: StateFlow<User?> = observeSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}
