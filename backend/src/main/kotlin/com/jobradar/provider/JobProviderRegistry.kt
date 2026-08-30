package com.jobradar.provider

import org.springframework.stereotype.Component

/**
 * Chooses which [JobProvider] the product uses, driven by config
 * (`jobradar.provider.key`, default "mock"). New sources (CSV, BOSS API, RSS…)
 * are added as [JobProvider] beans and registered here — the UI and business
 * layer never know where data comes from.
 */
@Component
class JobProviderRegistry(
    private val providers: List<JobProvider>,
    private val props: ProviderProperties,
) {
    /** The active provider by configured key; falls back to "mock" then first. */
    val active: JobProvider
        get() = providers.firstOrNull { it.key == props.key }
            ?: providers.firstOrNull { it.key == "mock" }
            ?: providers.first()

    val all: List<JobProvider> get() = providers
}
