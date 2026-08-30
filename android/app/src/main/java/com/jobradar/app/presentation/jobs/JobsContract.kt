package com.jobradar.app.presentation.jobs

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState
import com.jobradar.app.domain.model.JobFilter
import com.jobradar.app.domain.usecase.JobUi

/** MVI contract for the opportunity feed (Tab2). */
interface JobsContract : MviContract<JobsContract.State, JobsContract.Event, JobsContract.Effect> {

    data class State(
        val filter: JobFilter = JobFilter(),
        val jobs: List<JobUi> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
    ) : MviState

    sealed interface Event : MviEvent {
        object OnEnter : Event
        object Refresh : Event
        data class OnSortChange(val filter: JobFilter) : Event
        data class OnJobClick(val jobId: Long) : Event
    }

    sealed interface Effect : MviEffect {
        data class NavigateToJob(val jobId: Long) : Effect
    }
}
