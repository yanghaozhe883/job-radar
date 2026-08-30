package com.jobradar.app.data.repository

import com.jobradar.app.data.remote.WebSocketClient
import com.jobradar.app.data.remote.parsePushEvent
import com.jobradar.app.domain.model.JobPushSignal
import com.jobradar.app.domain.repository.JobPushRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Maps the raw WebSocket payloads to domain [JobPushSignal]s.
 *
 * The transport ([WebSocketClient]) is framework-only; parsing to a domain model
 * happens here so the rest of the app (and the radar UI) never sees raw JSON.
 */
@Singleton
class JobPushRepositoryImpl @Inject constructor(
    private val client: WebSocketClient,
) : JobPushRepository {

    override fun observeSignals(): Flow<JobPushSignal> =
        client.pushEvents().mapNotNull { raw ->
            parsePushEvent(raw)?.let { p ->
                JobPushSignal(jobId = p.jobId, title = p.title, matchScore = p.matchScore)
            }
        }
}
