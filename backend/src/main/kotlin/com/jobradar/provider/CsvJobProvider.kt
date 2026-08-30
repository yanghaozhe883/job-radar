package com.jobradar.provider

import com.jobradar.provider.ProviderProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

/**
 * CSV-backed provider — a real, pluggable data source that reads a simple
 * CSV file (title,city,salary_min_k,salary_max_k,skills;company). Lets the
 * product serve "real" data without a scraper or DB. Swap the active provider
 * in config (`jobradar.provider.key=csv`) and the UI doesn't change.
 */
@Component
class CsvJobProvider(
    private val props: ProviderProperties,
) : JobProvider {

    private val log = LoggerFactory.getLogger(CsvJobProvider::class.java)
    override val key: String = "csv"

    private val cache: List<ProviderJob> by lazy { load() }

    private fun load(): List<ProviderJob> {
        val path = props.csvPath
        if (path.isBlank() || !File(path).exists()) {
            log.info("csv provider: no file at '{}' — returning empty", path)
            return emptyList()
        }
        return File(path).readLines()
            .drop(1) // header
            .mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size < 5) null else {
                    ProviderJob(
                        id = "csv-${parts[4].trim()}",
                        title = parts[0].trim(),
                        city = parts[1].trim(),
                        salaryMinK = (parts[2].trim().toIntOrNull() ?: 0),
                        salaryMaxK = (parts[3].trim().toIntOrNull() ?: 0),
                        skills = parts[4].trim().split("|").filter { it.isNotBlank() },
                        companyName = if (parts.size > 5) parts[5].trim() else null,
                        source = key,
                    )
                }
            }
    }

    override fun search(filter: JobSearchFilter): List<ProviderJob> {
        val kw = filter.keyword?.lowercase()
        return cache
            .filter { filter.city == null || it.city == filter.city }
            .filter { filter.minSalaryK == null || it.salaryMaxK >= filter.minSalaryK }
            .filter { kw == null || it.title.lowercase().contains(kw) }
            .let { list ->
                val from = ((filter.page - 1).coerceAtLeast(0)) * filter.pageSize
                if (from >= list.size) emptyList() else list.subList(from, minOf(from + filter.pageSize, list.size))
            }
    }

    override fun detail(id: String): ProviderJob? = cache.firstOrNull { it.id == id }

    override fun company(name: String): ProviderCompany? =
        cache.firstOrNull { it.companyName == name }?.let { ProviderCompany(name = name, city = it.city) }

    override fun count(): Int = cache.size
}
