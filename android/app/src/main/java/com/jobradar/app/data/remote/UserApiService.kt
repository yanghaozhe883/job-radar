package com.jobradar.app.data.remote

import com.jobradar.app.data.remote.dto.AuthRequest
import com.jobradar.app.data.remote.dto.PreferenceDto
import com.jobradar.app.data.remote.dto.UserDto
import com.jobradar.app.data.remote.dto.UserJobDto
import com.jobradar.app.data.remote.dto.UserJobRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit contract for the auth + user-data endpoints (login, preferences,
 * favorites/applied). Mirrors the backend `UserController`.
 */
interface UserApiService {

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: AuthRequest): ApiResponse<UserDto>

    @GET("/api/v1/users/{id}")
    suspend fun getUser(@Path("id") id: Long): ApiResponse<UserDto>

    @GET("/api/v1/users/{id}/jobs")
    suspend fun getUserJobs(
        @Path("id") id: Long,
        @Query("status") status: String? = null,
    ): ApiResponse<List<UserJobDto>>

    @PUT("/api/v1/users/{id}/jobs/{jobId}/status")
    suspend fun setUserJob(
        @Path("id") id: Long,
        @Path("jobId") jobId: Long,
        @Body request: UserJobRequest,
    ): ApiResponse<UserJobDto>

    @GET("/api/v1/users/{id}/preferences")
    suspend fun getPreference(@Path("id") id: Long): ApiResponse<PreferenceDto>

    @PUT("/api/v1/users/{id}/preferences")
    suspend fun savePreference(
        @Path("id") id: Long,
        @Body pref: PreferenceDto,
    ): ApiResponse<PreferenceDto>
}
