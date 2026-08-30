package com.jobradar.app.presentation.favorites

import androidx.lifecycle.viewModelScope
import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.usecase.ObserveFavoriteJobsUseCase
import com.jobradar.app.domain.usecase.RefreshFavoriteJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoriteJobs: ObserveFavoriteJobsUseCase,
    private val refreshFavoriteJobs: RefreshFavoriteJobsUseCase,
) : MviViewModel<FavoritesContract.State, FavoritesContract.Event, FavoritesContract.Effect>(FavoritesContract.State()) {

    init {
        observeFavoriteJobs()
            .onEach { jobs -> reduce { it.copy(jobs = jobs, isLoading = false) } }
            .launchIn(viewModelScope)
        // Pull the user's favorites from the backend so the tab reflects server state.
        viewModelScope.launch { refreshFavoriteJobs() }
    }

    override suspend fun handleEvent(event: FavoritesContract.Event) {
        when (event) {
            is FavoritesContract.Event.OnEnter -> reduce { it.copy(isLoading = true) }
            is FavoritesContract.Event.OnJobClick ->
                emitEffect(FavoritesContract.Effect.NavigateToJob(event.jobId))
        }
    }
}
