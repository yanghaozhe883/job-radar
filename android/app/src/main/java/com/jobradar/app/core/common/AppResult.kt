package com.jobradar.app.core.common

/**
 * A deferred Result wrapper used across the app.
 *
 * Kept app-specific (rather than using kotlin.Result directly) so the domain
 * layer can carry a friendly, UI-agnostic error enum and stay fully decoupled
 * from any framework. This is an opinionated part of the Clean Architecture
 * contract: nothing in `domain` touches Android types.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>

    fun isSuccess(): Boolean = this is Success
}

/** UI-agnostic error model. Each maps to a string resource / prompt in the UI layer. */
sealed class AppError(open val id: String) {
    data object Network : AppError("network")
    data object Timeout : AppError("timeout")
    data object NotFound : AppError("not_found")
    data object Unauthorized : AppError("unauthorized")
    data object Unknown : AppError("unknown")
    data class Server(val status: Int, override val id: String = "server_$status") : AppError(id)
}
