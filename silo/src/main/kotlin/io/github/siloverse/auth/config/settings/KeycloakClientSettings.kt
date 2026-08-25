package io.github.siloverse.auth.config.settings

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
data class KeycloakClientSettings(
    val baseUrl: String
)