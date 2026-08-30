package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.repository.JobRepository
import com.jobradar.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Surface the newest, best-matching jobs the "radar" has detected.
 * The heartbeat of the product — it drives the "new opportunity detected"
 * moment on the radar screen.
 */
class ObserveRadarHitsUseCase @Inject constructor(
    private val jobRepository: JobRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val scoreJob: ScoreJobUseCase,
) {
    operator fun invoke(count: Int = 5): Flow<List<JobUi>> =
        combine(
            jobRepository.observeRadarHits(count),
            preferencesRepository.observePreferences(),
        ) { jobs, preference ->
            jobs.map { job -> JobUi(job, scoreJob(job, preference)) }
                .sortedByDescending { it.score.total }
        }
}
