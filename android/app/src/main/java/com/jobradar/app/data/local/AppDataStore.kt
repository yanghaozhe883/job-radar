package com.jobradar.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Single shared DataStore instance for the app.
 *
 * AndroidX DataStore forbids multiple active instances for the same file, so
 * every repository that needs persistent key-value storage must use this one
 * delegate. Defining it once here (rather than per-repository) is what prevents
 * the "multiple DataStores active for the same file" crash.
 */
val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "jobradar_prefs")
