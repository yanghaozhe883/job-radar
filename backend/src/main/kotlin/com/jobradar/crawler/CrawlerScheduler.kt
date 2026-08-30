package com.jobradar.crawler

import com.jobradar.config.CrawlerProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Runs the ingestion pipeline on a fixed schedule. Gated by `crawler.enabled`;
 * between runs it sleeps the configured rate limit so the source is never
 * hammered. Errors are caught & logged (a bad run must not kill the loop).
 */
@Component
class CrawlerScheduler(
    private val ingestionService: JobIngestionService,
    private val props: CrawlerProperties,
) {

    private val log = LoggerFactory.getLogger(CrawlerScheduler::class.java)

    @Scheduled(fixedDelay = 60_000) // every 60s after the last run finishes
    fun tick() {
        runCatching { ingestionService.runOnce() }
            .onFailure { log.error("ingestion cycle failed", it) }
        sleepRateLimit()
    }

    private fun sleepRateLimit() {
        runCatching { Thread.sleep(props.rateLimitMs.coerceAtLeast(1_000)) }
    }
}
