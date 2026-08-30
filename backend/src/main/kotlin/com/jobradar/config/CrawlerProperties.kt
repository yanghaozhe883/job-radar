package com.jobradar.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Crawler configuration (compliance-first).
 *
 * Enforced by design (see 方向总纲 §9.1):
 *  - OFF by default — enabling real scraping is an explicit, conscious act.
 *  - Rate-limited & user-agent-identified (politeness + traceability).
 *  - Only public pages; never touches login state, user data, or personal info.
 *  - Every ingested job is stamped with its `dataSource` for provenance.
 */
@ConfigurationProperties(prefix = "crawler")
data class CrawlerProperties(
    /** Master switch. When false no source runs. */
    val enabled: Boolean = false,

    /** Which source to run: "demo" (built-in sample) or "http" (configurable). */
    val source: String = "demo",

    /** Minimum delay between requests in ms (rate limiting / politeness). */
    val rateLimitMs: Long = 3_000,

    /** User-Agent identifying the collector as an honest crawler. */
    val userAgent: String = "JobRadarBot/1.0 (+https://jobradar.demo; respectful crawler)",

    /** Whether to honour robots.txt (best-effort; enforced at the source level). */
    val respectRobots: Boolean = true,

    /** For the "http" source: the target public list endpoint / feed. */
    val targetUrl: String? = null,
)
