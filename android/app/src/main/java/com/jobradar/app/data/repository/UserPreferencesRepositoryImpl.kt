package com.jobradar.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.data.local.SessionManager
import com.jobradar.app.data.local.appDataStore
import com.jobradar.app.data.mapper.toDto
import com.jobradar.app.data.mapper.toDomain
import com.jobradar.app.data.remote.UserApiService
import com.jobradar.app.domain.model.UserPreference
import com.jobradar.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Preference repository. Local cache in the shared [appDataStore]; also syncs to
 * the backend when a user is signed in (best-effort — offline edits stay local).
 */
@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userApi: UserApiService,
    private val sessionManager: SessionManager,
) : UserPreferencesRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val key = stringPreferencesKey("user_preference")

    override fun observePreferences(): Flow<UserPreference> =
        context.appDataStore.data.map { prefs ->
            prefs[key]?.let { raw -> runCatching { json.decodeFromString<UserPreference>(raw) }.getOrNull() }
                ?: UserPreference.DEFAULT
        }

    override suspend fun getPreferences(): UserPreference {
        val prefs = context.appDataStore.data.first()
        val raw = prefs[key]
        return raw?.let { runCatching { json.decodeFromString<UserPreference>(it) }.getOrNull() }
            ?: UserPreference.DEFAULT
    }

    override suspend fun updatePreferences(preference: UserPreference): AppResult<Unit> =
        try {
            context.appDataStore.edit { it[key] = json.encodeToString(preference) }
            syncToBackend(preference)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(com.jobradar.app.core.common.AppError.Unknown)
        }

    /** Best-effort push of the preference to the backend. Never blocks success. */
    private suspend fun syncToBackend(preference: UserPreference) {
        val userId = sessionManager.userId()
        if (userId <= 0) return
        runCatching { userApi.savePreference(userId, preference.toDto()) }
    }
}
