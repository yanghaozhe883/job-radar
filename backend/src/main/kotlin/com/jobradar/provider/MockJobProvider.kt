package com.jobradar.provider

import org.springframework.stereotype.Component

/**
 * v0.1 default provider. Returns a small in-memory data set so the whole
 * product works end-to-end offline (and so CI/demo is deterministic). This is a
 * MOCK — it models what a real source returns, never a scraper.
 *
 * The set mirrors the Web app mock so both ends show identical content.
 */
@Component
class MockJobProvider : JobProvider {

    override val key: String = "mock"

    private val data: List<ProviderJob> = listOf(
        job("1", "iOS 开发工程师", "北", "上海", 26, 45, listOf("Swift", "SwiftUI"), "北辰科技"),
        job("2", "产品经理", "北", "上海", 25, 45, listOf("产品规划", "用户研究"), "北辰科技"),
        job("3", "算法工程师", "深", "深圳", 35, 55, listOf("Python", "LLM"), "深云信息"),
        job("4", "后端开发（Kotlin）", "瀚", "北京", 30, 50, listOf("Kotlin", "Spring"), "瀚海数据"),
        job("5", "AI 应用工程师（实习）", "芥", "杭州", 8, 12, listOf("RAG", "Agent"), "芥子科技"),
        job("6", "前端开发（Next.js）", "云", "成都", 22, 40, listOf("Next.js", "TypeScript"), "云端科技"),
        job("7", "智能体工程师", "灵", "上海", 40, 70, listOf("Multi-Agent", "Tool Calling"), "灵犀实验室"),
        job("8", "运维工程师", "中", "石家庄", 10, 16, listOf("Linux", "Docker"), "中电智广"),
        job("9", "数据采集工程师", "北", "北京", 20, 35, listOf("Python", "Scrapy", "SQL"), "北辰数据"),
        job("10", "数据产品经理", "上", "上海", 25, 45, listOf("数据产品", "SQL", "用户增长"), "上海数据"),
    )

    override fun search(filter: JobSearchFilter): List<ProviderJob> {
        val kw = filter.keyword?.lowercase()
        return data
            .filter { filter.city == null || it.city == filter.city }
            .filter { filter.jobType == null || it.jobType == filter.jobType }
            .filter { filter.minSalaryK == null || it.salaryMaxK >= filter.minSalaryK }
            .filter { kw == null || it.title.lowercase().contains(kw) || (it.companyName?.lowercase()?.contains(kw) == true) }
            .let { list ->
                val from = ((filter.page - 1).coerceAtLeast(0)) * filter.pageSize
                if (from >= list.size) emptyList() else list.subList(from, minOf(from + filter.pageSize, list.size))
            }
    }

    override fun detail(id: String): ProviderJob? = data.firstOrNull { it.id == id }

    override fun company(name: String): ProviderCompany? =
        data.firstOrNull { it.companyName == name }?.let {
            ProviderCompany(name = name, city = it.city, industry = "互联网", size = "100-500人")
        }

    override fun count(): Int = data.size

    private fun job(
        id: String,
        title: String,
        logo: String,
        city: String,
        minK: Int,
        maxK: Int,
        skills: List<String>,
        company: String,
    ) = ProviderJob(
        id = id,
        title = title,
        city = city,
        salaryMinK = minK,
        salaryMaxK = maxK,
        skills = skills,
        description = "$title（Mock 数据，来自 $key 源）。用于产品演示与端手联调，可插拔替换为真实数据源。",
        companyName = company,
        companyLogoUrl = null,
        source = key,
    )
}
