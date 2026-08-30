package com.jobradar.app.presentation.radar

import androidx.lifecycle.viewModelScope
import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.model.JobPushSignal
import com.jobradar.app.domain.usecase.ObserveJobPushSignalsUseCase
import com.jobradar.app.domain.usecase.ObserveRadarHitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for the Radar screen. Emits one-shot effects for navigation and
 * haptics; observes the radar hits stream into state. UI never touches business
 * logic — it only sends [RadarContract.Event]s.
 *
 * Also subscribes to the backend WebSocket push signals: when a new high-match
 * job is detected server-side, the radar flashes its detection moment and
 * vibrates, then refreshes the hits feed.
 */
@HiltViewModel
class RadarViewModel @Inject constructor(
    private val observeRadarHits: ObserveRadarHitsUseCase,
    private val observePushSignals: ObserveJobPushSignalsUseCase,
) : MviViewModel<RadarContract.State, RadarContract.Event, RadarContract.Effect>(RadarContract.State()) {

    init {
        collectRadar()
        collectPushSignals()
    }

    private fun collectRadar() {
        observeRadarHits(5)
            .onEach { hits ->
                val last = hits.firstOrNull()?.job?.id
                reduce { it.copy(radarHits = hits, isLoading = false, lastDetectedJobId = last) }
            }
            .launchIn(viewModelScope)
    }

    private fun collectPushSignals() {
        observePushSignals()
            .onEach { signal -> onLivePush(signal) }
            .launchIn(viewModelScope)
    }

    private fun onLivePush(signal: JobPushSignal) {
        // Mark the detection moment (drives the radar flash + glow) and vibrate.
        reduce {
            it.copy(
                liveSignal = signal,
                lastDetectedJobId = signal.jobId,
            )
        }
        emitEffect(RadarContract.Effect.VibrateOnDetection)
        emitEffect(RadarContract.Effect.Toast("新机会：${signal.title ?: "职位"}"))
    }

    override suspend fun handleEvent(event: RadarContract.Event) {
        when (event) {
            is RadarContract.Event.OnEnter -> reduce { it.copy(isLoading = true) }
            is RadarContract.Event.Refresh -> viewModelScope.launch {
                reduce { it.copy(isLoading = true) }
                reduce { it.copy(isLoading = false) }
            }
            is RadarContract.Event.OnJobClick ->
                emitEffect(RadarContract.Effect.NavigateToJob(event.jobId))
            is RadarContract.Event.OnLiveSignal -> onLivePush(event.signal)
        }
    }
}
