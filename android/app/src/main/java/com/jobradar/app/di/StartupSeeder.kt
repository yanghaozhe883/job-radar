package com.jobradar.app.di

import com.jobradar.app.domain.usecase.RefreshJobsUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * App-start bootstrap. On launch it triggers a real refresh against the backend
 * so the local cache is populated by the time a screen opens. This app never
 * seeds demo/mock data — every job comes from the real API; if the backend is
 * down the screens surface an error state instead.
 *
 * Per-screen refreshes are also driven by their ViewModels; this only ensures a
 * fresh cache exists even before the first screen triggers its own refresh.
 */
@Singleton
class StartupSeeder @Inject constructor(
    private val refreshJobs: RefreshJobsUseCase,
    @ApplicationScope private val scope: CoroutineScope,
) {
    fun start() {
        scope.launch {
            refreshJobs()
        }
    }
}
