package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observe the current radar targeting profile. */
class ObservePreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<UserPreference> = repository.observePreferences()
}
