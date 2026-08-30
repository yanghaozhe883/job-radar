package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.JobPushSignal
import com.jobradar.app.domain.repository.JobPushRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observe the real-time radar push signals from the backend WebSocket. */
class ObserveJobPushSignalsUseCase @Inject constructor(
    private val repository: JobPushRepository,
) {
    operator fun invoke(): Flow<JobPushSignal> = repository.observeSignals()
}
