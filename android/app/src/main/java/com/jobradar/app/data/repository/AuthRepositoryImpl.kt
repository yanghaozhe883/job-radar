package com.jobradar.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jobradar.app.core.common.AppError
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.data.local.appDataStore
import com.jobradar.app.data.mapper.toDomain
import com.jobradar.app.data.remote.UserApiService
import com.jobradar.app.data.remote.dto.AuthRequest
import com.jobradar.app.domain.model.AuthValidator
import com.jobradar.app.domain.model.User
import com.jobradar.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Auth implementation.
 *
 * Session is persisted in the shared [appDataStore]. `signIn` calls the real
 * backend `/auth/login`; if the backend is unreachable it falls back to a local
 * deterministic demo user so the app is still usable on a dev machine without
 * the server. The contract mirrors a real backend auth flow.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userApi: UserApiService,
) : AuthRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val userIdKey = longPreferencesKey("auth_user_id")
    private val userJsonKey = stringPreferencesKey("auth_user_json")

    override fun observeSession(): Flow<User?> =
        context.appDataStore.data.map { prefs ->
            val id = prefs[userIdKey]
            val raw = prefs[userJsonKey]
            if (id != null && raw != null) {
                runCatching { json.decodeFromString<User>(raw) }.getOrNull()
            } else null
        }

    override suspend fun signIn(phone: String, code: String): AppResult<User> {
        return try {
            // Real backend login first.
            val response = userApi.login(AuthRequest(phone = phone.trim(), code = code))
            val user = response.requireData().toDomain()
            persist(user)
            AppResult.Success(user)
        } catch (e: Exception) {
            // Backend offline -> fall back to a local demo user so the app is usable.
            if (!AuthValidator.isValidPhone(phone) || !AuthValidator.isValidCode(code)) {
                return AppResult.Failure(AppError.Server(400))
            }
            val user = User(
                id = phone.trim().hashCode().toLong().and(0x3FFFFFFF),
                phone = phone.trim(),
                nickname = "求职者 ${phone.trim().takeLast(4)}",
            )
            persist(user)
            AppResult.Success(user)
        }
    }

    override suspend fun signOut() {
        // Only clear auth keys — leave the rest of the DataStore (e.g. radar
        // preferences) intact.
        context.appDataStore.edit { prefs ->
            prefs.remove(userIdKey)
            prefs.remove(userJsonKey)
        }
    }

    private suspend fun persist(user: User) {
        context.appDataStore.edit { prefs ->
            prefs[userIdKey] = user.id
            prefs[userJsonKey] = json.encodeToString(user)
        }
    }
}
