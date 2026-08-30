package com.jobradar.provider

import org.springframework.boot.context.properties.ConfigurationProperties

/** Binds the `jobradar.provider` block — selects which [JobProvider] is active. */
@ConfigurationProperties(prefix = "jobradar.provider")
data class ProviderProperties(
    /** The active provider key: "mock" (default) | "csv" | future plugins. */
    val key: String = "mock",
    /** Optional CSV path for the [CsvJobProvider]. */
    val csvPath: String = "",
)
