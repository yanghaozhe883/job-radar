package com.jobradar.app.domain.repository

import com.jobradar.app.domain.model.JobPushSignal
import kotlinx.coroutines.flow.Flow

/**
 * Source of real-time "new opportunity detected" signals, fed by the backend's
 * WebSocket push stream. The Radar screen subscribes to this so it can flash
 * the detection moment, vibrate, and refresh its hits — all without polling.
 */
interface JobPushRepository {
    /** Hot flow of push signals. Emits as the backend broadcasts new jobs. */
    fun observeSignals(): Flow<JobPushSignal>
}
