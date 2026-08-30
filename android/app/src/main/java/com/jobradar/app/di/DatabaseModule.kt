package com.jobradar.app.di

import android.content.Context
import androidx.room.Room
import com.jobradar.app.data.local.JobRadarDatabase
import com.jobradar.app.data.local.dao.JobDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JobRadarDatabase =
        Room.databaseBuilder(
            context,
            JobRadarDatabase::class.java,
            "jobradar.db",
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideJobDao(db: JobRadarDatabase): JobDao = db.jobDao()
}
