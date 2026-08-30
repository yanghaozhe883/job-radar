package com.jobradar.app.di

import com.jobradar.app.data.local.dao.JobDao
import com.jobradar.app.data.repository.AiRepositoryImpl
import com.jobradar.app.data.repository.AuthRepositoryImpl
import com.jobradar.app.data.repository.JobPushRepositoryImpl
import com.jobradar.app.data.repository.JobRepositoryImpl
import com.jobradar.app.data.repository.UserPreferencesRepositoryImpl
import com.jobradar.app.domain.repository.AiRepository
import com.jobradar.app.domain.repository.AuthRepository
import com.jobradar.app.domain.repository.InMemoryResumeRepository
import com.jobradar.app.domain.repository.JobPushRepository
import com.jobradar.app.domain.repository.JobRepository
import com.jobradar.app.domain.repository.ResumeRepository
import com.jobradar.app.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the domain repository contracts to their data-layer implementations.
 * This is the central seam of Clean Architecture: presentation depends only on
 * the `domain.repository` interfaces; Hilt supplies the impls.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindJobRepository(impl: JobRepositoryImpl): JobRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindJobPushRepository(impl: JobPushRepositoryImpl): JobPushRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository

    @Binds
    @Singleton
    abstract fun bindResumeRepository(impl: InMemoryResumeRepository): ResumeRepository
}
