package com.jobradar.app

import android.app.Application
import com.jobradar.app.di.RadarPushConnector
import com.jobradar.app.di.StartupSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class JobRadarApplication : Application() {

    @Inject
    lateinit var startupSeeder: StartupSeeder

    @Inject
    lateinit var radarPushConnector: RadarPushConnector

    override fun onCreate() {
        super.onCreate()
        // Seed the local cache so the UI is never blank, even offline / fresh install.
        startupSeeder.start()
        // Open the real-time push socket to the backend so the radar can light up.
        radarPushConnector.start()
    }
}
