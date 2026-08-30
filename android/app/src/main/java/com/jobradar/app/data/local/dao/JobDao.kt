package com.jobradar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jobradar.app.data.local.entity.JobWithStatusEntity
import com.jobradar.app.data.local.entity.JobEntity
import com.jobradar.app.data.local.entity.UserJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Upsert
    suspend fun upsertJobs(jobs: List<JobEntity>)

    @Upsert
    suspend fun upsertJob(job: JobEntity)

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: Long): JobEntity?

    @Query(
        """
        SELECT j.*, s.status AS status FROM jobs j
        LEFT JOIN user_jobs s ON s.jobId = j.id
        WHERE (:city IS NULL OR j.city = :city)
          AND (:keyword IS NULL OR j.title LIKE '%' || :keyword || '%')
        ORDER BY j.matchScore DESC, j.publishedAt DESC
        LIMIT :limit
        """
    )
    fun observeJobs(city: String?, keyword: String?, limit: Int): Flow<List<JobWithStatusEntity>>

    @Query("SELECT * FROM jobs ORDER BY publishedAt DESC LIMIT :limit")
    fun observeRadarHits(limit: Int): Flow<List<JobEntity>>

    @Query(
        """
        SELECT * FROM jobs WHERE id IN (
            SELECT jobId FROM user_jobs WHERE status = 'FAVORITE'
        ) ORDER BY publishedAt DESC
        """
    )
    fun observeFavoriteJobs(): Flow<List<JobEntity>>

    @Upsert
    suspend fun upsertUserJob(userJob: UserJobEntity)

    @Query("SELECT * FROM user_jobs WHERE jobId = :jobId")
    fun observeJobStatus(jobId: Long): Flow<UserJobEntity?>

    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun count(): Int
}
