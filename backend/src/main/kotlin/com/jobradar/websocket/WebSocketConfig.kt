package com.jobradar.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * Registers the real-time job push endpoint. The Android client connects to
 * `ws://host:8080/jobs/stream` (see NetworkModule WS_URL).
 */
@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val jobPushHandler: JobPushHandler,
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(jobPushHandler, "/jobs/stream")
            .setAllowedOrigins("*")
    }
}
