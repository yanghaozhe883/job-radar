package com.jobradar.insight

import com.fasterxml.jackson.databind.ObjectMapper
import com.jobradar.ai.AiService
import com.jobradar.config.LlmProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.ArgumentMatchers.anyString

class AnythingLlmInsightProviderTest {

    private val aiService: AiService = mock(AiService::class.java)
    private val provider = AnythingLlmInsightProvider(
        aiService,
        ObjectMapper(),
        LlmProperties(baseUrl = "http://127.0.0.1:3001", apiKey = "x", workspaceSlug = "job-radar"),
    )

    private val job = InsightJob(
        id = "1", title = "AI 应用工程师", city = "上海",
        salaryMinK = 30, salaryMaxK = 50, jobType = "全职",
        experience = "1-3年", education = "本科",
        skills = listOf("Kotlin", "RAG"), description = "做一个 AI 产品",
        companyName = "北辰科技",
    )
    private val profile = UserProfile(
        targetRoles = listOf("AI 应用开发"),
        skills = listOf("Kotlin", "Python"),
        yearsOfExperience = 2,
    )

    @Test
    fun `valid JSON is parsed into a model insight`() {
        val json = """
            {"responsibilities":["负责 AI 产品研发"],"coreSkills":["Kotlin","RAG"],
             "riskPoints":["需要快速迭代"],"growth":["通往架构师"],
             "whyRecommended":["和你技能高度匹配"],
             "match":{"skillMatch":85,"experienceMatch":70,"directionMatch":90,
               "skillReason":"技能重合高","experienceReason":"经验基本符合","directionReason":"方向契合"}}
        """.trimIndent()
        `when`(aiService.chat(anyString(), anyString(), anyString()))
            .thenReturn(AiService.AiReply(answer = json, model = "qwen"))

        val insight = provider.generate(job, profile)

        assertEquals("model", insight.generatedBy)
        assertEquals(85, insight.match.skillMatch)
        assertEquals(90, insight.match.directionMatch)
        assertEquals(82, insight.match.overall)
        assertEquals(listOf("Kotlin", "RAG"), insight.coreSkills)
    }

    @Test
    fun `parse failure degrades to fallback, not hard-fabricated`() {
        `when`(aiService.chat(anyString(), anyString(), anyString()))
            .thenReturn(AiService.AiReply(answer = "这是很不错的岗位，建议投递……", model = "qwen"))

        val insight = provider.generate(job, profile)

        assertEquals("fallback", insight.generatedBy)
        assertTrue(insight.responsibilities.isEmpty())
        assertEquals(0, insight.match.skillMatch)
        assertTrue(insight.whyRecommended.isEmpty())
    }
}
