package com.jobradar.app.domain.repository

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Auth contract. Keeps the login flow decoupled: the UI only calls [signIn] and
 * observes [observeSession]; persistence and validation live in the impl.
 */
interface AuthRepository {

    /** Emits the current signed-in user (null when logged out). */
    fun observeSession(): Flow<User?>

    /** Sign in with a phone + verification code. Returns the user on success. */
    suspend fun signIn(phone: String, code: String): AppResult<User>

    /** Sign out and clear the persisted session. */
    suspend fun signOut()
}
