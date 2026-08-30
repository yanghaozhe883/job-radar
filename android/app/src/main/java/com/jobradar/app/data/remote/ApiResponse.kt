package com.jobradar.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The shared backend envelope: { code, message, data }.
 *
 * Every backend endpoint returns this wrapper (agreed contract). The client
 * unwraps it in one place so the rest of the codebase only deals with [data].
 *
 * @param code  0 = success; non-zero = business error.
 * @param message human-readable message (success or error).
 * @param data   the typed payload (list/map/model via [T]).
 */
@Serializable
data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null,
) {
    val isSuccess: Boolean get() = code == 0

    /** Returns [data] or throws an [ApiException] with a friendly message. */
    fun requireData(): T =
        data ?: throw ApiException(code, message.ifBlank { "服务器返回数据为空" })
}

/** A business-level error surfaced by the backend envelope. */
class ApiException(
    val code: Int,
    override val message: String,
) : Exception(message)

/** Paginated list payload (used by job streams). */
@Serializable
data class PageDto<T>(
    val items: List<T> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 20,
    @SerialName("has_more") val hasMore: Boolean = false,
)
