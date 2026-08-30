package com.jobradar.app.domain.repository

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobFilter
import kotlinx.coroutines.flow.Flow

/**
 * The single source-of-truth for job data, expressed as a domain contract.
 *
 * The UI and ViewModel only ever talk to this interface. The concrete
 * implementation (combining remote + local) lives in the `data` layer, and is
 * injected via Hilt. This is the seam that keeps UI fully decoupled from
 * networking/persistence.
 */
interface JobRepository {
    /** Observe the stream of jobs matching [filter]. */
    fun observeJobs(filter: JobFilter): Flow<List<Job>>

    /** One-shot fetch (used by pull-to-refresh / retry). */
    suspend fun refreshJobs(filter: JobFilter): AppResult<List<Job>>

    suspend fun getJobById(id: Long): Job?

    /** Persist a user<->job interaction (favorite / apply / hide / seen). */
    suspend fun setJobStatus(jobId: Long, status: com.jobradar.app.domain.model.JobStatus)

    fun observeJobStatus(jobId: Long): Flow<com.jobradar.app.domain.model.JobStatus?>

    /** The newest jobs surfaced by the radar (sorted by recency + match). */
    fun observeRadarHits(count: Int): Flow<List<Job>>

    /** Observe the user's favorited jobs for the Favorites tab. */
    fun observeFavoriteJobs(): Flow<List<Job>>

    /** Pull the user's favorited jobs from the backend and persist to cache. */
    suspend fun refreshFavoriteJobs(): AppResult<List<Job>>
}
