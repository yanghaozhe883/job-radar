package com.jobradar.app.presentation.favorites

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState
import com.jobradar.app.domain.usecase.JobUi

/** MVI contract for the Favorites tab (Tab3). */
interface FavoritesContract : MviContract<FavoritesContract.State, FavoritesContract.Event, FavoritesContract.Effect> {

    data class State(
        val jobs: List<JobUi> = emptyList(),
        val isLoading: Boolean = false,
    ) : MviState

    sealed interface Event : MviEvent {
        object OnEnter : Event
        data class OnJobClick(val jobId: Long) : Event
    }

    sealed interface Effect : MviEffect {
        data class NavigateToJob(val jobId: Long) : Effect
    }
}
