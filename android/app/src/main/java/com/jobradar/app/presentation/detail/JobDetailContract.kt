package com.jobradar.app.presentation.detail

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobInsight
import com.jobradar.app.domain.model.JobStatus
import com.jobradar.app.domain.model.MatchScore

/** MVI contract for the Job Detail screen. */
interface JobDetailContract : MviContract<JobDetailContract.State, JobDetailContract.Event, JobDetailContract.Effect> {

    data class State(
        val job: Job? = null,
        val score: MatchScore? = null,
        val status: JobStatus? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        /** v0.3 · Insight result, or null while loading / not yet loaded. */
        val insight: JobInsight? = null,
        val insightState: InsightUiState = InsightUiState.Loading,
    ) : MviState

    sealed interface Event : MviEvent {
        object OnEnter : Event
        object ToggleFavorite : Event
        object Apply : Event
        object OnBack : Event
    }

    sealed interface Effect : MviEffect {
        object Back : Effect
        data class Toast(val message: String) : Effect
    }
}

/** UI state for the Insight card (loading / loaded / degraded / error). */
enum class InsightUiState { Loading, Loaded, Degraded, Error }
