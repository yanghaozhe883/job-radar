package com.jobradar.crawler

import com.jobradar.config.CrawlerProperties
import org.springframework.stereotype.Component

/**
 * Built-in demo source. Returns a small sample of jobs so the ingestion pipeline
 * works end-to-end offline (and so CI tests are deterministic). This is NOT a
 * scraper — it models what a real source would return. Swap to [HttpJobSource]
 * for actual public feeds.
 */
@Component
class DemoJobSource(
    private val props: CrawlerProperties,
) : JobSource {

    override val key: String = "demo"

    override fun fetch(): List<RawJob> = listOf(
        raw(1L, "数据采集工程师", "北京", 20, 35, listOf("Python", "Scrapy", "Kafka", "SQL")),
        raw(2L, "爬虫开发工程师", "深圳", 22, 38, listOf("Python", "Scrapy", "反爬", "分布式")),
        raw(3L, "数据产品经理", "上海", 25, 45, listOf("数据产品", "SQL", "用户增长")),
    )

    private fun raw(
        id: Long,
        title: String,
        city: String,
        minK: Int,
        maxK: Int,
        skills: List<String>,
    ) = RawJob(
        externalId = "demo-$id",
        title = title,
        city = city,
        salaryMinK = minK,
        salaryMaxK = maxK,
        skills = skills,
        description = "$title（演示数据，来自 $key 源）。负责真实职位数据的抓取与入库，遵循限流与 robots 规范。",
        companyName = "$city 数据科技",
        source = key,
    )
}
