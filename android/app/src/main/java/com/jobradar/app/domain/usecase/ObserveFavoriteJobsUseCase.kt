package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.repository.JobRepository
import com.jobradar.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** Observe the user's favorited jobs, scored against preferences. */
class ObserveFavoriteJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val scoreJob: ScoreJobUseCase,
) {
    operator fun invoke(): Flow<List<JobUi>> =
        combine(
            jobRepository.observeFavoriteJobs(),
            preferencesRepository.observePreferences(),
        ) { jobs, preference ->
            jobs.map { job -> JobUi(job, scoreJob(job, preference)) }
                .sortedByDescending { it.score.total }
        }
}
