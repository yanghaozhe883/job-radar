package com.jobradar.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

/**
 * A minimal broadcast WebSocket handler. When a new job is detected (e.g. soon
 * after scraping), the backend pushes { jobId, title, matchScore } to every
 * connected client so the Android radar can light up in real time.
 */
@Component
class JobPushHandler(
    private val mapper: ObjectMapper,
) : TextWebSocketHandler() {

    private val log = LoggerFactory.getLogger(JobPushHandler::class.java)
    private val sessions = java.util.concurrent.ConcurrentHashMap<String, WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session
        log.info("WebSocket connected: {}", session.id)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session.id)
        log.info("WebSocket closed: {} ({})", session.id, status)
    }

    /** Broadcast a push event to all connected clients. */
    fun broadcast(event: JobPushEvent) {
        val payload = mapper.writeValueAsString(event)
        sessions.values.forEach { session ->
            runCatching { if (session.isOpen) session.sendMessage(TextMessage(payload)) }
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // Ping from client; reply pong to keep the connection alive.
        runCatching { session.sendMessage(TextMessage("pong")) }
    }
}

/** Payload pushed to clients on new-job detection. */
data class JobPushEvent(
    val jobId: Long,
    val title: String? = null,
    val matchScore: Int? = null,
)
