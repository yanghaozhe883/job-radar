package com.jobradar.app.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobStatus
import com.jobradar.app.domain.usecase.GetJobDetailUseCase
import com.jobradar.app.domain.usecase.GetJobInsightUseCase
import com.jobradar.app.domain.usecase.ObserveJobStatusUseCase
import com.jobradar.app.domain.usecase.ObservePreferencesUseCase
import com.jobradar.app.domain.usecase.ScoreJobUseCase
import com.jobradar.app.domain.usecase.SetJobStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Job Detail screen.
 *
 * The match score is computed against the *live* user preference (not a hardcoded
 * default), so it always reflects the current radar profile — consistent with the
 * feed/radar scoring. The job detail is loaded once; the preference stream is
 * combined to re-score whenever the profile changes.
 */
@HiltViewModel
class JobDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getJobDetail: GetJobDetailUseCase,
    private val observeJobStatus: ObserveJobStatusUseCase,
    private val setJobStatus: SetJobStatusUseCase,
    private val observePreferences: ObservePreferencesUseCase,
    private val scoreJob: ScoreJobUseCase,
    private val getJobInsight: GetJobInsightUseCase,
) : MviViewModel<JobDetailContract.State, JobDetailContract.Event, JobDetailContract.Effect>(JobDetailContract.State()) {

    private val jobId: Long = savedStateHandle["jobId"] ?: -1L
    private val validJobId: Boolean get() = jobId > 0

    private var cachedJob: Job? = null

    init {
        // Load the job once, then combine with the live preference to score.
        combine(observePreferences(), observeJobStatus(jobId)) { pref, status ->
            pref to status
        }.onEach { (pref, status) ->
            if (validJobId) {
                val job = cachedJob ?: getJobDetail(jobId).also { cachedJob = it }
                val score = job?.let { scoreJob(it, pref) }
                reduce { it.copy(job = job, score = score, status = status, isLoading = false) }
            } else {
                // Guard: if the deep link didn't carry a valid id, surface an error
                // instead of querying a bogus id / writing junk rows.
                reduce { it.copy(isLoading = false, error = "职位不存在") }
            }
        }.launchIn(viewModelScope)

        reduce { it.copy(isLoading = true) }
        loadInsight()
    }

    /** v0.3 · Load the explainable insight once (from the shared backend contract). */
    private fun loadInsight() {
        if (!validJobId) return
        viewModelScope.launch {
            reduce { it.copy(insightState = com.jobradar.app.presentation.detail.InsightUiState.Loading) }
            // pass the user's target / skills as the profile grounding (extendable).
            val insight = getJobInsight(jobId.toString())
            if (insight != null) {
                val state = if (insight.generatedBy == "fallback")
                    com.jobradar.app.presentation.detail.InsightUiState.Degraded
                else com.jobradar.app.presentation.detail.InsightUiState.Loaded
                reduce { it.copy(insight = insight, insightState = state) }
            } else {
                reduce { it.copy(insightState = com.jobradar.app.presentation.detail.InsightUiState.Error) }
            }
        }
    }

    override suspend fun handleEvent(event: JobDetailContract.Event) {
        when (event) {
            is JobDetailContract.Event.OnEnter -> reduce { it.copy(isLoading = true) }
            is JobDetailContract.Event.ToggleFavorite -> {
                if (!validJobId) return
                val current = state.value.status
                val newStatus = if (current == JobStatus.FAVORITE) JobStatus.SEEN else JobStatus.FAVORITE
                setJobStatus(jobId, newStatus)
                emitEffect(JobDetailContract.Effect.Toast(if (newStatus == JobStatus.FAVORITE) "已收藏" else "已取消收藏"))
            }
            is JobDetailContract.Event.Apply -> {
                if (!validJobId) return
                setJobStatus(jobId, JobStatus.APPLIED)
                emitEffect(JobDetailContract.Effect.Toast("投递成功，雷达已为你跟进"))
            }
            is JobDetailContract.Event.OnBack -> emitEffect(JobDetailContract.Effect.Back)
        }
    }
}
