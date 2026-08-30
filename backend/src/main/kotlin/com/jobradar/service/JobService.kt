package com.jobradar.service

import com.jobradar.api.ApiCode
import com.jobradar.api.ApiException
import com.jobradar.api.dto.JobDto
import com.jobradar.api.dto.PageDto
import com.jobradar.api.dto.SortOption
import com.jobradar.domain.JobEntity
import com.jobradar.repo.JobRepository
import com.jobradar.service.JobMapper.toDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Business logic for the job feed. Exposes the operations the controller needs
 * while keeping persistence concerns out of the web layer.
 */
@Service
class JobService(
    private val jobRepository: JobRepository,
) {

    @Transactional(readOnly = true)
    fun search(
        city: String?,
        keyword: String?,
        jobType: String?,
        minSalaryK: Int?,
        sort: SortOption,
        page: Int,
        pageSize: Int,
    ): PageDto<JobDto> {
        val entities = jobRepository.search(city, keyword, jobType, minSalaryK)
        val sorted = applySort(entities, sort)
        val total = sorted.size
        val from = ((page - 1).coerceAtLeast(0)) * pageSize
        val slice = if (from >= total) emptyList() else sorted.subList(from, minOf(from + pageSize, total))
        val items = slice.map { it.toDto(score = it.matchScore) }
        return PageDto(
            items = items,
            total = total,
            page = page,
            pageSize = pageSize,
            hasMore = from + pageSize < total,
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): JobDto {
        val entity = jobRepository.findById(id)
            .orElseThrow { ApiException(ApiCode.NOT_FOUND, "职位不存在") }
        return entity.toDto(score = entity.matchScore)
    }

    /** Radar: newest published jobs, scored against keywords (or a neutral profile). */
    @Transactional(readOnly = true)
    fun radarHits(count: Int, userId: Long?, keywords: List<String>?): List<JobDto> {
        val entities = jobRepository.findByOrderByPublishedAtDesc()
            .take(count.coerceAtLeast(1))
        return entities.map { entity ->
            val score = MatchScorer.score(
                title = entity.title,
                skills = entity.skills,
                salaryMinK = entity.salaryMinK,
                salaryMaxK = entity.salaryMaxK,
                keywords = keywords,
                baseline = 60, // rely on skill+salary, keep a fair floor for surfaced hits
            )
            entity.toDto(score = score)
        }
    }

    private fun applySort(entities: List<JobEntity>, sort: SortOption): List<JobEntity> = when (sort) {
        SortOption.LATEST -> entities.sortedByDescending { it.publishedAt ?: 0L }
        SortOption.SALARY -> entities.sortedByDescending { it.salaryMaxK }
        SortOption.MATCH -> entities.sortedByDescending { it.matchScore }
        SortOption.COMPREHENSIVE -> entities.sortedWith(
            compareByDescending<JobEntity> { it.matchScore }
                .thenByDescending { it.publishedAt ?: 0L }
        )
    }
}
