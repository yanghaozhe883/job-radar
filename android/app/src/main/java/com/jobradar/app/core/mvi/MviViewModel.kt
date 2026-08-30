package com.jobradar.app.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Generic MVI ViewModel base.
 *
 * Every feature's ViewModel extends this, passing its own contract. It exposes:
 *  - [state]: the single source of truth UI listens to.
 *  - [effect]: a one-shot event stream.
 *  - [onEvent]: the ONLY entry point the UI calls to deliver a user intent.
 *
 * Subclasses implement [handleEvent] as a pure reducer that returns a new
 * [S] (via [reduce]) or emits side effects (via [emitEffect]).
 */
abstract class MviViewModel<S : MviState, E : MviEvent, F : MviEffect>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<F>(Channel.BUFFERED)
    val effect: Flow<F> = _effect.receiveAsFlow()

    /** Entry point for every user intent. */
    fun onEvent(event: E) {
        viewModelScope.launch { handleEvent(event) }
    }

    /** Reducer: turn one [event] into a new [S] or an [F]. Implemented per feature. */
    protected abstract suspend fun handleEvent(event: E)

    /** Compose a new immutable state. MUST be called on main for UI consistency. */
    protected fun reduce(transform: (S) -> S) {
        _state.update(transform)
    }

    /** Emit a one-shot side effect. */
    protected fun emitEffect(effect: F) {
        _effect.trySend(effect)
    }
}
