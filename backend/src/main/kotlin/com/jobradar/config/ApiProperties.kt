package com.jobradar.config

import org.springframework.boot.context.properties.ConfigurationProperties

/** Binds the `api.base-path` property used by the controllers. */
@ConfigurationProperties(prefix = "api")
data class ApiProperties(
    val basePath: String = "/api/v1",
)
