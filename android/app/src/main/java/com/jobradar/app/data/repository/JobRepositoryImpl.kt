package com.jobradar.app.data.repository

import com.jobradar.app.core.common.AppError
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.data.local.SessionManager
import com.jobradar.app.data.local.dao.JobDao
import com.jobradar.app.data.local.entity.UserJobEntity
import com.jobradar.app.data.mapper.toDomain
import com.jobradar.app.data.mapper.toEntity
import com.jobradar.app.data.remote.ApiException
import com.jobradar.app.data.remote.JobApiService
import com.jobradar.app.data.remote.UserApiService
import com.jobradar.app.data.remote.dto.UserJobRequest
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobFilter
import com.jobradar.app.domain.model.JobStatus
import com.jobradar.app.domain.repository.JobRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default [JobRepository].
 *
 * Data flow strategy — **backend-first, no mock fallback**:
 *  1. The UI always reads from the local cache (Room) so it renders instantly.
 *  2. [refreshJobs] pulls from the real backend and persists to Room. If the
 *     backend is unreachable it returns a [AppResult.Failure] so the UI can
 *     surface an error — this app deliberately does NOT fall back to demo/junk
 *     data. Every job shown comes from the real API.
 *  3. User<->job interactions (favorite/applied/seen) sync to the backend via
 *     [UserApiService] when signed in, with Room as the local cache.
 */
@Singleton
class JobRepositoryImpl @Inject constructor(
    private val dao: JobDao,
    private val api: JobApiService,
    private val userApi: UserApiService,
    private val sessionManager: SessionManager,
) : JobRepository {

    override fun observeJobs(filter: JobFilter): Flow<List<Job>> =
        dao.observeJobs(filter.city, filter.keyword, limit = MAX_FEED)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshJobs(filter: JobFilter): AppResult<List<Job>> = try {
        val response = api.getJobs(
            city = filter.city,
            keyword = filter.keyword,
            minSalaryK = filter.minSalaryK,
            sort = filter.sort.name,
        )
        val dtos = response.requireData().items
        val domain = dtos.map { it.toDomain() }
        persistJobs(domain)
        AppResult.Success(domain)
    } catch (e: ApiException) {
        AppResult.Failure(AppError.Server(e.code))
    } catch (e: Exception) {
        AppResult.Failure(AppError.Network)
    }

    override suspend fun getJobById(id: Long): Job? = dao.getJobById(id)?.toDomain()

    override suspend fun setJobStatus(jobId: Long, status: JobStatus) {
        dao.upsertUserJob(
            UserJobEntity(jobId = jobId, status = status.name, updatedAt = System.currentTimeMillis())
        )
        syncStatusToBackend(jobId, status)
    }

    /** Best-effort push of the job status to the backend. Never blocks. */
    private suspend fun syncStatusToBackend(jobId: Long, status: JobStatus) {
        val userId = sessionManager.userId()
        if (userId <= 0) return
        runCatching {
            userApi.setUserJob(userId, jobId, UserJobRequest(jobId = jobId, status = status.name))
        }
    }

    override fun observeJobStatus(jobId: Long): Flow<JobStatus?> =
        dao.observeJobStatus(jobId).map { entity ->
            entity?.let { JobStatus.valueOf(it.status) }
        }

    override fun observeRadarHits(count: Int): Flow<List<Job>> =
        dao.observeRadarHits(count).map { entities -> entities.map { it.toDomain() } }

    override fun observeFavoriteJobs(): Flow<List<Job>> =
        dao.observeFavoriteJobs().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshFavoriteJobs(): AppResult<List<Job>> {
        val userId = sessionManager.userId()
        return try {
            if (userId <= 0) {
                AppResult.Success(emptyList())
            } else {
                val dtos = userApi.getUserJobs(userId, status = "FAVORITE").requireData()
                val domain = dtos.mapNotNull { it.job?.toDomain()?.also { job ->
                    dao.upsertUserJob(UserJobEntity(jobId = job.id, status = "FAVORITE", updatedAt = System.currentTimeMillis()))
                } }
                AppResult.Success(domain)
            }
        } catch (e: Exception) {
            AppResult.Failure(AppError.Network)
        }
    }

    // --- internals ---

    private suspend fun persistJobs(jobs: List<Job>) {
        if (jobs.isEmpty()) return
        dao.upsertJobs(jobs.map { it.toEntity() })
    }

    private companion object {
        const val MAX_FEED = 100
    }
}
