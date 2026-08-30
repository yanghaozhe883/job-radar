package com.jobradar.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jobradar.app.data.local.dao.JobDao
import com.jobradar.app.data.local.entity.JobEntity
import com.jobradar.app.data.local.entity.UserJobEntity

@Database(
    entities = [JobEntity::class, UserJobEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(RoomTypeConverters::class)
abstract class JobRadarDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
}
