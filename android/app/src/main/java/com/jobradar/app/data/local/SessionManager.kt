package com.jobradar.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jobradar.app.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cheap accessor for the current signed-in user id / session, shared by the
 * repositories that need to sync per-user data to the backend. Reads from the
 * same [appDataStore] the auth repository writes to.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val userIdKey = longPreferencesKey("auth_user_id")
    private val userJsonKey = stringPreferencesKey("auth_user_json")

    /** Current session, or null. */
    fun session(): Flow<User?> =
        context.appDataStore.data.map { prefs ->
            val id = prefs[userIdKey]
            val raw = prefs[userJsonKey]
            if (id != null && raw != null) {
                runCatching { json.decodeFromString<User>(raw) }.getOrNull()
            } else null
        }

    /** Current user id, or 0 when signed out. Non-suspending for quick checks. */
    suspend fun userId(): Long = session().first()?.id ?: 0L
}
