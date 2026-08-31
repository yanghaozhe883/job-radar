package com.jobradar.insight

import com.jobradar.domain.JobInsight
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * InsightService — the business-layer entry point (v0.3).
 * It does NOT know AnythingLLM / Ollama / 王庭. It only depends on [InsightProvider].
 * It also caches per-job results so we don't re-burn tokens on every open.
 */
@Service
class InsightService(
    private val provider: InsightProvider,
) {
    /** jobId -> cached insight. Simple in-memory TTL-less cache for v0.3. */
    private val cache = ConcurrentHashMap<String, CachedInsight>()

    fun generate(job: InsightJob, profile: UserProfile): JobInsight {
        val key = cacheKey(job)
        cache[key]?.let { cached ->
            // Return cache unless it's a fallback (a fallback is retried next time).
            if (cached.insight.generatedBy == "model") return cached.insight
        }
        val insight = provider.generate(job, profile)
        cache[key] = CachedInsight(System.currentTimeMillis(), insight)
        return insight
    }

    private fun cacheKey(job: InsightJob): String =
        "${job.id}|${job.title}|${job.skills.joinToString(",")}"
}

/** Simple wrapper: timestamp keeps the cache honest about freshness (can add TTL later). */
data class CachedInsight(val at: Long, val insight: JobInsight)
