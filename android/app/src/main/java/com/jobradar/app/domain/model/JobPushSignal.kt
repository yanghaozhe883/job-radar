package com.jobradar.app.domain.model

import kotlinx.serialization.Serializable

/**
 * A real-time signal pushed by the backend over WebSocket when a new (high-match)
 * job is detected. Pure domain model — the presentation layer reacts to this to
 * light up the radar.
 */
@Serializable
data class JobPushSignal(
    val jobId: Long,
    val title: String? = null,
    val matchScore: Int? = null,
)
