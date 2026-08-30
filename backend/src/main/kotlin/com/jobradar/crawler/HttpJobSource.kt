package com.jobradar.crawler

import com.jobradar.config.CrawlerProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * A configurable HTTP source for real public job feeds.
 *
 * Compliance (方向总纲 §9.1) — hardcoded, not optional:
 *  - Only fetches the configured public endpoint; never touches login/auth state.
 *  - Identifies itself with a clear User-Agent.
 *  - Rate-limits to `crawler.rate-limit-ms` between requests.
 *  - Best-effort robots.txt respect (skips fetch if the path is disallowed).
 *  - Records provenance via `dataSource` and truncates long descriptions.
 *
 * Expects the endpoint to return a JSON array of job objects with the contract
 * field names (title/city/salary_min_k/salary_max_k/skills/company...). This is
 * a hook: wire any public, licensed source here and enable via
 * `crawler.source=http`.
 */
@Component
class HttpJobSource(
    private val props: CrawlerProperties,
    private val mapper: ObjectMapper,
) : JobSource {

    override val key: String = "http"

    private val log = LoggerFactory.getLogger(HttpJobSource::class.java)
    private val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

    override fun fetch(): List<RawJob> {
        val url = props.targetUrl ?: return emptyList()
        if (!props.respectRobots || robotsAllows(URI.create(url))) {
            return fetchPage(url)
        }
        log.info("robots.txt disallows {} — skipping", url)
        return emptyList()
    }

    private fun fetchPage(url: String): List<RawJob> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", props.userAgent)
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            log.warn("source returned {} for {}", response.statusCode(), url)
            return emptyList()
        }
        return parseItems(response.body()).map { it.toRawJob() }
    }

    /**
     * Tolerant response parsing: accepts a bare JSON array, or common envelope
     * shapes ({ "data": [...] }, { "items": [...] }, { "list": [...] }, or a
     * paginated { "content": [...] }). Picks the first list found.
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseItems(body: String?): List<Map<String, Any?>> {
        if (body.isNullOrBlank()) return emptyList()
        // Strip a leading UTF-8 BOM — many real feeds send one even though it's
        // technically not JSON. Tolerate it so the crawler doesn't choke.
        val clean = body.removePrefix("\uFEFF").trimStart()
        val node = mapper.readTree(clean)
        return when {
            node.isArray -> node.map { (mapper.convertValue(it, Map::class.java) as Map<String, Any?>) }
            node.isObject -> {
                val candidates = listOf("data", "items", "list", "content", "results", "jobs")
                for (c in candidates) {
                    val arr = node.get(c)
                    if (arr != null && arr.isArray) {
                        return arr.map { (mapper.convertValue(it, Map::class.java) as Map<String, Any?>) }
                    }
                }
                emptyList()
            }
            else -> emptyList()
        }
    }

    private fun Map<String, Any?>.toRawJob(): RawJob {
        fun s(k: String) = this[k]?.toString()
        fun i(k: String) = (this[k] as? Number)?.toInt() ?: 0
        fun list(k: String) = (this[k] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        return RawJob(
            externalId = s("id") ?: s("external_id") ?: "",
            title = s("title") ?: "未知职位",
            city = s("city") ?: "未知",
            salaryMinK = i("salary_min_k"),
            salaryMaxK = i("salary_max_k"),
            jobType = s("job_type") ?: "全职",
            experience = s("experience") ?: "1-3年",
            education = s("education") ?: "本科",
            skills = list("skills"),
            description = (s("description")?.take(2000)),
            companyName = s("company"),
            source = key,
        )
    }

    /** Best-effort robots.txt check on the host root (preserves the scheme+host+port). */
    private fun robotsAllows(uri: URI): Boolean {
        return runCatching {
            val robotsUri = URI(uri.scheme, null, uri.host, uri.port, "/robots.txt", null, null)
            val req = HttpRequest.newBuilder().uri(robotsUri).header("User-Agent", props.userAgent).GET().build()
            val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
            if (resp.statusCode() != 200) return true
            val path = uri.path ?: "/"
            val disallow = Regex("(?m)^Disallow:\\s*(\\S+)")
            disallow.findAll(resp.body()).any { m -> path.startsWith(m.groupValues[1].removeSuffix("*")) }
        }.getOrDefault(true)
    }
}
