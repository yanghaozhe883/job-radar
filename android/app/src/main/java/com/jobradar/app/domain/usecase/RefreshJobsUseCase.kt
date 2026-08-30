package com.jobradar.app.domain.usecase

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobFilter
import com.jobradar.app.domain.repository.JobRepository
import javax.inject.Inject

/**
 * Pull the latest jobs from the backend and persist them to the local cache.
 *
 * The UI always reads from [JobRepository.observeJobs] (local cache), so this is
 * the single "source of truth refresh" path: it fetches real data (falling back
 * to seeded mock when offline) and writes it back to Room, which then emits to
 * the UI. This is the seam that turns the app from mock-only into real-API.
 */
class RefreshJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository,
) {
    suspend operator fun invoke(filter: JobFilter = JobFilter()): AppResult<List<Job>> =
        jobRepository.refreshJobs(filter)
}
