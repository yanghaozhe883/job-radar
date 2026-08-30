package com.jobradar.app.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A lightweight OkHttp WebSocket client wrapping the backend's real-time push
 * endpoint. Emits raw payload strings; the repository maps them to domain
 * models. Designed as an interface + impl so the socket can be faked in tests.
 *
 * Note: this wires the transport. When the backend is online, pass the real
 * `ws://.../jobs/stream` URL; the rest of the app observes [pushEvents] only.
 */
interface WebSocketClient {
    fun connect(url: String)
    fun disconnect()
    fun pushEvents(): Flow<String>
}

@Singleton
class OkHttpWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
) : WebSocketClient {

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 64)
    private var socket: WebSocket? = null

    override fun connect(url: String) {
        if (socket != null) return
        val request = Request.Builder().url(url).build()
        socket = okHttpClient.newWebSocket(request, listener)
    }

    override fun disconnect() {
        socket?.close(1000, "client shutdown")
        socket = null
    }

    override fun pushEvents(): Flow<String> = _events.asSharedFlow()

    private val listener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            _events.tryEmit(text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            // Log / schedule reconnect in production. Kept minimal for the scaffold.
            socket?.cancel()
            socket = null
        }
    }
}

/** Simplest possible payload a push event could carry (job id + title). */
@Serializable
data class JobPushEvent(
    val jobId: Long,
    val title: String? = null,
    val matchScore: Int? = null,
)

/** Parse a pushed string payload into a [JobPushEvent]. */
fun parsePushEvent(raw: String): JobPushEvent? = runCatching {
    Json.decodeFromString<JobPushEvent>(raw)
}.getOrNull()
