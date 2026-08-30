package com.jobradar.api

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * The shared API envelope: { code, message, data }.
 *
 * This is the single response shape every endpoint returns. It mirrors the
 * Android client's `data/remote/ApiResponse.kt` (see 方向总纲 §8.2).
 *
 * @param code   0 = success; non-zero = business error.
 * @param message human-readable message.
 * @param data   typed payload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "ok",
    val data: T? = null,
) {
    companion object {
        fun <T> ok(data: T? = null, message: String = "ok") = ApiResponse(0, message, data)

        fun <T> error(code: Int, message: String, data: T? = null) = ApiResponse(code, message, data)
    }
}

/** Standard business error codes. */
object ApiCode {
    const val OK = 0
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val INTERNAL = 500
}

/** A business-level exception carrying an [ApiCode]. */
class ApiException(
    val code: Int = ApiCode.INTERNAL,
    message: String = "服务器内部错误",
) : RuntimeException(message)
