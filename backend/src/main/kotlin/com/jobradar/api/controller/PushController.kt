package com.jobradar.api.controller

import com.jobradar.api.ApiResponse
import com.jobradar.websocket.JobPushEvent
import com.jobradar.websocket.JobPushHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Demo endpoint that broadcasts a real-time push over WebSocket so the Android
 * radar "detects" a new opportunity live. Useful for demos and for the client
 * integration test — a real deployment would broadcast inside the job-ingestion
 * pipeline instead (e.g. when the scraper finds a new job).
 */
@RestController
@RequestMapping("\${api.base-path:/api/v1}/push")
class PushController(
    private val pushHandler: JobPushHandler,
) {

    @PostMapping("/demo")
    fun demo(@RequestBody event: JobPushEvent): ApiResponse<String> {
        pushHandler.broadcast(event)
        return ApiResponse.ok("已广播", "push ok")
    }
}
