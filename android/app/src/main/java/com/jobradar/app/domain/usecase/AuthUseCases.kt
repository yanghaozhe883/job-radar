package com.jobradar.app.domain.usecase

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.model.User
import com.jobradar.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observe the current signed-in session (null = logged out). */
class ObserveSessionUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<User?> = repository.observeSession()
}

/** Sign in with a phone + verification code. */
class SignInUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(phone: String, code: String): AppResult<User> =
        repository.signIn(phone, code)
}

/** Sign out. */
class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke() = repository.signOut()
}
