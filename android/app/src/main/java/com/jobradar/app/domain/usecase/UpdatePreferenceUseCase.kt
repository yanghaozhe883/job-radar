package com.jobradar.app.domain.usecase

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/** Persist a change to the radar targeting profile (e.g. after editing chips). */
class UpdatePreferenceUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(preference: UserPreference): AppResult<Unit> =
        repository.updatePreferences(preference)
}
