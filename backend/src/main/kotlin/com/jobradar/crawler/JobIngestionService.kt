package com.jobradar.crawler

import com.jobradar.config.CrawlerProperties
import com.jobradar.domain.CompanyEntity
import com.jobradar.domain.JobEntity
import com.jobradar.repo.CompanyRepository
import com.jobradar.repo.JobRepository
import com.jobradar.websocket.JobPushEvent
import com.jobradar.websocket.JobPushHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * The ingestion pipeline:
 *   fetch (source) -> normalize -> de-duplicate -> persist -> broadcast new jobs
 *
 * Runs the configured [JobSource] behind `crawler.enabled`. Every new job that
 * wasn't already stored is saved and broadcast over WebSocket so the radar can
 * light up in real time.
 */
@Service
class JobIngestionService(
    private val props: CrawlerProperties,
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val jobPushHandler: JobPushHandler,
    private val demoSource: DemoJobSource,
    private val httpSource: HttpJobSource,
) {

    private val log = LoggerFactory.getLogger(JobIngestionService::class.java)

    /** Resolve the source keyed by `crawler.source`. */
    private fun source(): JobSource = when (props.source) {
        "http" -> httpSource
        else -> demoSource
    }

    /** Run one ingestion cycle. Returns the number of NEW jobs ingested. */
    @Transactional
    fun runOnce(): Int {
        if (!props.enabled) {
            log.info("crawler disabled — skipping ingestion. Set crawler.enabled=true to enable.")
            return 0
        }
        val src = source()
        val raw = src.fetch()
        log.info("[{}] fetched {} jobs", src.key, raw.size)

        var inserted = 0
        raw.forEach { rawJob ->
            if (isDuplicate(rawJob)) return@forEach
            val job = persist(rawJob)
            inserted++
            broadcastNew(job)
        }
        log.info("[{}] ingested {} new jobs", src.key, inserted)
        return inserted
    }

    private fun isDuplicate(rawJob: RawJob): Boolean {
        if (rawJob.externalId.isBlank()) return false
        return jobRepository.findByDataSourceAndSourceExternalId(rawJob.source, rawJob.externalId).isPresent
    }

    private fun persist(rawJob: RawJob): JobEntity {
        val company = rawJob.companyName?.let { name ->
            companyRepository.findByName(name).orElseGet {
                companyRepository.save(
                    CompanyEntity(name = name, city = rawJob.city, logoUrl = rawJob.companyLogoUrl)
                )
            }
        }
        val entity = JobEntity(
            title = rawJob.title,
            city = rawJob.city,
            salaryMinK = rawJob.salaryMinK,
            salaryMaxK = rawJob.salaryMaxK,
            jobType = rawJob.jobType,
            experience = rawJob.experience,
            education = rawJob.education,
            skills = rawJob.skills.toMutableList(),
            description = rawJob.description,
            company = company,
            dataSource = rawJob.source,
            sourceExternalId = rawJob.externalId,
            publishedAt = rawJob.publishedAt,
        )
        return jobRepository.save(entity)
    }

    private fun broadcastNew(job: JobEntity) {
        jobPushHandler.broadcast(
            JobPushEvent(jobId = job.id ?: 0L, title = job.title, matchScore = job.matchScore)
        )
    }
}
