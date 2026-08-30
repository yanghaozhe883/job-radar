package com.jobradar.app.domain.usecase

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.repository.JobRepository
import javax.inject.Inject

/** Pull the user's favorited jobs from the backend and refresh the cache. */
class RefreshFavoriteJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository,
) {
    suspend operator fun invoke(): AppResult<List<Job>> = jobRepository.refreshFavoriteJobs()
}
