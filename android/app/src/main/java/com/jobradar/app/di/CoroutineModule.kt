package com.jobradar.app.di

import com.jobradar.app.core.dispatcher.AppDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/** Marks the application-scoped [CoroutineScope]. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    /**
     * A long-lived scope tied to the application, used for app-lifecycle work
     * like seeding the database on startup. Survives Activity/fragment scopes.
     */
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(dispatcher: AppDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatcher.io)
}
