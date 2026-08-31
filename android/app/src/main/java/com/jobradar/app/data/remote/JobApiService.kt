package com.jobradar.app.data.remote

import com.jobradar.app.data.remote.dto.JobDto
import com.jobradar.app.data.remote.dto.JobInsightDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit contract for the job backend.
 *
 * All endpoints return the shared [ApiResponse] envelope. A single data source
 * flag allows the UI/mock + real backend to coexist during development; when
 * the backend is ready, only the base URL in [NetworkModule] changes.
 */
interface JobApiService {

    @GET("/api/v1/jobs")
    suspend fun getJobs(
        @Query("city") city: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("job_type") jobType: String? = null,
        @Query("min_salary_k") minSalaryK: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
    ): ApiResponse<PageDto<JobDto>>

    @GET("/api/v1/jobs/{id}")
    suspend fun getJob(@Path("id") id: Long): ApiResponse<JobDto>

    /** Newest jobs surfaced by the radar (used by the radar screen). */
    @GET("/api/v1/jobs/radar/hits")
    suspend fun getRadarHits(
        @Query("count") count: Int = 5,
        @Query("user_id") userId: Long? = null,
    ): ApiResponse<List<JobDto>>

    /** v0.3 · Insight — ask the backend (AI) to explain a job for this user. */
    @GET("/api/v1/jobs/{id}/insight")
    suspend fun getInsight(
        @Path("id") id: String,
        @Query("target_roles") targetRoles: List<String>? = null,
        @Query("skills") skills: List<String>? = null,
        @Query("years_of_experience") yearsOfExperience: Int? = null,
    ): ApiResponse<JobInsightDto>
}
