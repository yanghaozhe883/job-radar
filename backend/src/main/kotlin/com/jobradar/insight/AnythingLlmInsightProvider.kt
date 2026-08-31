package com.jobradar.insight

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.jobradar.ai.AiService
import com.jobradar.config.LlmProperties
import com.jobradar.domain.JobInsight
import com.jobradar.domain.MatchInsight
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AnythingLlmInsightProvider(
    private val aiService: AiService,
    private val mapper: ObjectMapper,
    private val llm: LlmProperties,
) : InsightProvider {

    private val log = LoggerFactory.getLogger(AnythingLlmInsightProvider::class.java)

    override fun generate(job: InsightJob, profile: UserProfile): JobInsight {
        val prompt = buildPrompt(job, profile)
        // retry once on parse failure
        var lastRaw: String? = null
        repeat(2) { attempt ->
            try {
                // Explicit 3-arg call so Mockito stubbers don't trip the default-arg
                // path that reads `props` (null in tests). Real runtime injects it.
                val reply = aiService.chat(prompt, "chat", llm.workspaceSlug)
                val parsed = parse(reply.answer)
                if (parsed != null) return parsed.copy(generatedBy = "model", model = reply.model)
                lastRaw = reply.answer
                log.warn("Insight parse failed on attempt {}; retrying", attempt + 1)
            } catch (e: Exception) {
                lastRaw = e.message
                log.warn("Insight generate error on attempt {}: {}", attempt + 1, e.message)
            }
        }
        // Explicit degradation — do NOT hard-fabricate a "valid" object.
        log.warn("Insight unavailable after retry. raw/first 200={}", lastRaw?.take(200))
        return JobInsight(jobId = job.id, generatedBy = "fallback")
    }

    private fun buildPrompt(job: InsightJob, profile: UserProfile): String {
        val userSkill = if (profile.skills.isNotEmpty()) profile.skills.joinToString("、") else "（未提供）"
        val userTarget = if (profile.targetRoles.isNotEmpty()) profile.targetRoles.joinToString("、") else "（未提供）"
        val userExp = if (profile.yearsOfExperience > 0) "${profile.yearsOfExperience} 年" else "（未提供）"
        val userCity = profile.city ?: "（未提供）"
        return """
            你是求职岗位洞察助手。请根据下面的【职位】和【求职者画像】，输出一个 JSON 对象。
            只输出 JSON 本身，不要 markdown 代码块，不要任何解释或前后缀。

            硬性事实约束（必须严格遵守）：
            1. 只能使用【职位】和【求职者画像】中【明确提供】的事实；缺失的信息一律写成“未提供 / 无法判断”，严禁推测或补全。
            2. 严禁从技能推断从业年限、工作经历、公司经历、学历、行业经历 —— 例如“掌握 Kotlin”不等于“有 Kotlin 工作经验”，“做过项目”不等于“有 N 年经验”。
            3. 若画像某字段为“未提供”，匹配度该维度取 0，reason 写“用户未提供该信息，无法判断”。
            4. 所有 match reason 必须能追溯到上面给出的具体输入事实，不得凭空断言。

            输出字段（严格固定，键名见下）：
            {
              "responsibilities": ["一句话职责1", "..."],
              "coreSkills": ["核心技能1", "..."],
              "riskPoints": ["风险点1", "..."],
              "growth": ["成长点1", "..."],
              "whyRecommended": ["为什么推荐给该求职者1", "..."],
              "match": {
                "skillMatch": 0,
                "experienceMatch": 0,
                "directionMatch": 0,
                "skillReason": "可追溯到输入的理由",
                "experienceReason": "可追溯到输入的理由",
                "directionReason": "可追溯到输入的理由"
              }
            }
            三个匹配度取 0..100 整数，务必给出 reason，且 reason 只能引用输入中真实存在的事实。

            【职位】
            标题：${job.title}
            城市：${job.city}
            薪资：${job.salaryMinK}-${job.salaryMaxK}K
            类型：${job.jobType} / 经验要求：${job.experience} / 学历要求：${job.education}
            技能：${job.skills.joinToString("、")}
            公司：${job.companyName ?: "未知"}
            描述：${job.description?.take(800) ?: "无"}

            【求职者画像】
            目标方向：$userTarget
            已有技能：$userSkill
            经验年限：$userExp
            目标城市：$userCity
        """.trimIndent()
    }

    /** Strict parse of the AI's JSON. Returns null if it is not a clean JSON object. */
    private fun parse(raw: String?): JobInsight? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        return try {
            val node: JsonNode = mapper.readTree(cleaned)
            val match = node.get("match")
            JobInsight(
                jobId = "",  // filled by caller if needed
                responsibilities = node.get("responsibilities")?.mapNotNull { it.asText() } ?: emptyList(),
                coreSkills = node.get("coreSkills")?.mapNotNull { it.asText() } ?: emptyList(),
                riskPoints = node.get("riskPoints")?.mapNotNull { it.asText() } ?: emptyList(),
                growth = node.get("growth")?.mapNotNull { it.asText() } ?: emptyList(),
                whyRecommended = node.get("whyRecommended")?.mapNotNull { it.asText() } ?: emptyList(),
                match = if (match != null) MatchInsight(
                    skillMatch = match.get("skillMatch")?.asInt() ?: 0,
                    experienceMatch = match.get("experienceMatch")?.asInt() ?: 0,
                    directionMatch = match.get("directionMatch")?.asInt() ?: 0,
                    skillReason = match.get("skillReason")?.asText() ?: "",
                    experienceReason = match.get("experienceReason")?.asText() ?: "",
                    directionReason = match.get("directionReason")?.asText() ?: "",
                ) else MatchInsight(),
            )
        } catch (e: Exception) {
            log.warn("Insight JSON parse threw: {}", e.message)
            null
        }
    }
}
