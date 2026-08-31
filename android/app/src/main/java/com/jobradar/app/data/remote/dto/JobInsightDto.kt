package com.jobradar.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirror of backend `JobInsight` — make a job understandable. */
@Serializable
data class JobInsightDto(
    val jobId: String = "",
    val responsibilities: List<String> = emptyList(),
    val coreSkills: List<String> = emptyList(),
    val riskPoints: List<String> = emptyList(),
    val growth: List<String> = emptyList(),
    val whyRecommended: List<String> = emptyList(),
    val match: MatchInsightDto = MatchInsightDto(),
    val model: String? = null,
    val generatedBy: String = "model",
)

@Serializable
data class MatchInsightDto(
    @SerialName("skillMatch")
    val skillMatch: Int = 0,
    @SerialName("experienceMatch")
    val experienceMatch: Int = 0,
    @SerialName("directionMatch")
    val directionMatch: Int = 0,
    @SerialName("skillReason")
    val skillReason: String = "",
    @SerialName("experienceReason")
    val experienceReason: String = "",
    @SerialName("directionReason")
    val directionReason: String = "",
    @SerialName("overall")
    val overall: Int = 0,
)
