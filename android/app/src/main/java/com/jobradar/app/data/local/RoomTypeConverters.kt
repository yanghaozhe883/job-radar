package com.jobradar.app.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room type converters.
 *
 * The domain model's `List<String>` (e.g. job skills) has no native SQL
 * representation; we serialize to/from JSON for storage. Kept in the data layer
 * so the domain model stays clean.
 */
class RoomTypeConverters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = runCatching {
        json.decodeFromString<List<String>>(value)
    }.getOrDefault(emptyList())
}
