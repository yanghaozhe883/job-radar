package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobFilter
import com.jobradar.app.domain.model.MatchScore
import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.repository.JobRepository
import com.jobradar.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Observe the jobs feed, enriched with each job's live [MatchScore].
 *
 * Combines the raw job stream with the user preference stream, scoring on the
 * fly so the UI is always consistent with the latest profile. Keeping this in a
 * UseCase (not the ViewModel) preserves the Clean Architecture rule that
 * business logic lives outside the View layer.
 */
class ObserveJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val scoreJob: ScoreJobUseCase,
) {
    /** Emits [JobUi] = job + its score. */
    operator fun invoke(filter: JobFilter): Flow<List<JobUi>> =
        combine(
            jobRepository.observeJobs(filter),
            preferencesRepository.observePreferences(),
        ) { jobs, preference ->
            jobs.map { job -> JobUi(job, scoreJob(job, preference)) }
        }
}

/** Presentation-ready pairing of a job with its live match score. */
data class JobUi(
    val job: Job,
    val score: MatchScore,
)
