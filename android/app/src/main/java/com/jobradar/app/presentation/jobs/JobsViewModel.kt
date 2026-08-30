package com.jobradar.app.presentation.jobs

import androidx.lifecycle.viewModelScope
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.model.JobFilter
import com.jobradar.app.domain.usecase.ObserveJobsUseCase
import com.jobradar.app.domain.usecase.RefreshJobsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val observeJobs: ObserveJobsUseCase,
    private val refreshJobs: RefreshJobsUseCase,
) : MviViewModel<JobsContract.State, JobsContract.Event, JobsContract.Effect>(JobsContract.State()) {

    private var currentFilter: JobFilter = JobFilter()

    // Track the latest filter via a private field (MVI keeps state immutable);
    // re-subscribe when the filter changes.
    private var jobFlow: kotlinx.coroutines.Job? = null

    init {
        collectJobs(currentFilter)
        refresh()
    }

    private fun collectJobs(filter: JobFilter) {
        jobFlow?.cancel()
        jobFlow = observeJobs(filter)
            .onEach { jobs ->
                reduce { it.copy(jobs = jobs, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)
    }

    /** Pull from the real backend and reflect success/error in state. */
    private fun refresh() {
        viewModelScope.launch {
            reduce { it.copy(isLoading = true) }
            when (val result = refreshJobs(currentFilter)) {
                is AppResult.Success -> {
                    collectJobs(currentFilter)
                    reduce { it.copy(isLoading = false, error = null) }
                }
                is AppResult.Failure -> {
                    reduce { it.copy(isLoading = false, error = "连接服务器失败，请检查网络后重试") }
                }
            }
        }
    }

    override suspend fun handleEvent(event: JobsContract.Event) {
        when (event) {
            is JobsContract.Event.OnEnter -> reduce { it.copy(isLoading = true) }
            is JobsContract.Event.Refresh -> refresh()
            is JobsContract.Event.OnSortChange -> {
                currentFilter = event.filter
                collectJobs(currentFilter)
            }
            is JobsContract.Event.OnJobClick ->
                emitEffect(JobsContract.Effect.NavigateToJob(event.jobId))
        }
    }
}
