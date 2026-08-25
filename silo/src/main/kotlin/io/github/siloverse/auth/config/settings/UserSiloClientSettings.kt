package io.github.siloverse.auth.config.settings

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "user-silo")
data class UserSiloClientSettings(
    val baseUrl: String
)