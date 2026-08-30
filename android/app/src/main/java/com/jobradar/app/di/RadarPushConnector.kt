package com.jobradar.app.di

import com.jobradar.app.BuildConfig
import com.jobradar.app.data.remote.WebSocketClient
import com.jobradar.app.domain.repository.JobPushRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns the lifetime of the WebSocket push connection. Called once on app start
 * to open the socket to the backend; the radar UI then subscribes to the parsed
 * signals via [JobPushRepository.observeSignals].
 *
 * Kept as a singleton bridge so connection management stays out of the UI layer.
 */
@Singleton
class RadarPushConnector @Inject constructor(
    private val client: WebSocketClient,
    @ApplicationScope private val scope: CoroutineScope,
) {

    private var started = false

    /** Open the socket if not already connected. Safe to call multiple times. */
    fun start() {
        if (started) return
        started = true
        scope.launch {
            client.connect(BuildConfig.WS_URL)
        }
    }
}
