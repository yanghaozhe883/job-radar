package com.jobradar.app.di

import com.jobradar.app.core.dispatcher.AppDispatcher
import com.jobradar.app.core.dispatcher.DefaultAppDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    fun provideDispatcher(): AppDispatcher = DefaultAppDispatcher()
}
