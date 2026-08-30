package com.jobradar.app.domain.repository

import com.jobradar.app.core.common.AppResult
import com.jobradar.app.domain.model.UserPreference
import kotlinx.coroutines.flow.Flow

/** Repository for the user's targeting profile. */
interface UserPreferencesRepository {
    fun observePreferences(): Flow<UserPreference>
    suspend fun getPreferences(): UserPreference
    suspend fun updatePreferences(preference: UserPreference): AppResult<Unit>
}
