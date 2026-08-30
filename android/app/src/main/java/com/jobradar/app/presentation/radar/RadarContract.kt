package com.jobradar.app.presentation.radar

import com.jobradar.app.core.mvi.MviContract
import com.jobradar.app.core.mvi.MviEffect
import com.jobradar.app.core.mvi.MviEvent
import com.jobradar.app.core.mvi.MviState
import com.jobradar.app.domain.model.JobPushSignal
import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.usecase.JobUi

/** MVI contract for the Radar screen (the product's heart). */
interface RadarContract : MviContract<RadarContract.State, RadarContract.Event, RadarContract.Effect> {

    data class State(
        val preference: UserPreference = UserPreference.DEFAULT,
        val radarHits: List<JobUi> = emptyList(),
        val isLoading: Boolean = false,
        val lastDetectedJobId: Long? = null,
        /** Last push signal (backend WebSocket). Drives the live detection moment. */
        val liveSignal: JobPushSignal? = null,
    ) : MviState

    sealed interface Event : MviEvent {
        object OnEnter : Event
        object Refresh : Event
        data class OnJobClick(val jobId: Long) : Event
        /** A push signal was just received from the backend. */
        data class OnLiveSignal(val signal: JobPushSignal) : Event
    }

    sealed interface Effect : MviEffect {
        data class NavigateToJob(val jobId: Long) : Effect
        object VibrateOnDetection : Effect
        data class Toast(val message: String) : Effect
    }
}
