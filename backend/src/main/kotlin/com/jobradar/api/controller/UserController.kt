package com.jobradar.api.controller

import com.jobradar.api.ApiResponse
import com.jobradar.api.dto.AuthRequest
import com.jobradar.api.dto.PreferenceDto
import com.jobradar.api.dto.UserDto
import com.jobradar.api.dto.UserJobDto
import com.jobradar.api.dto.UserJobRequest
import com.jobradar.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Auth + user endpoints that make the client's local-only data (session,
 * preferences, favorites/applied) backend-backed:
 *   POST /api/v1/auth/login
 *   GET  /api/v1/users/{id}
 *   GET  /api/v1/users/{id}/jobs?status=FAVORITE|APPLIED
 *   PUT  /api/v1/users/{id}/jobs/{jobId}/status
 *   GET  /api/v1/users/{id}/preferences
 *   PUT  /api/v1/users/{id}/preferences
 */
@RestController
@RequestMapping("\${api.base-path:/api/v1}")
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/auth/login")
    fun login(@RequestBody request: AuthRequest): ApiResponse<UserDto> =
        ApiResponse.ok(userService.signIn(request.phone, request.code))

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): ApiResponse<UserDto> =
        ApiResponse.ok(userService.getUser(id))

    @GetMapping("/users/{id}/jobs")
    fun getUserJobs(
        @PathVariable id: Long,
        @RequestParam(required = false) status: String?,
    ): ApiResponse<List<UserJobDto>> =
        ApiResponse.ok(userService.listUserJobs(id, status))

    @PutMapping("/users/{id}/jobs/{jobId}/status")
    fun setUserJob(
        @PathVariable id: Long,
        @PathVariable jobId: Long,
        @RequestBody request: UserJobRequest,
    ): ApiResponse<UserJobDto> =
        ApiResponse.ok(userService.setJobStatus(id, jobId, request.status))

    @GetMapping("/users/{id}/preferences")
    fun getPreference(@PathVariable id: Long): ApiResponse<PreferenceDto> =
        ApiResponse.ok(userService.getPreference(id))

    @PutMapping("/users/{id}/preferences")
    fun savePreference(
        @PathVariable id: Long,
        @RequestBody pref: PreferenceDto,
    ): ApiResponse<PreferenceDto> =
        ApiResponse.ok(userService.savePreference(id, pref))
}
