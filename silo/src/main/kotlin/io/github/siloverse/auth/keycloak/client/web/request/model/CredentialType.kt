package io.github.siloverse.auth.keycloak.client.web.request.model

import com.fasterxml.jackson.annotation.JsonValue

enum class CredentialType(
    @get:JsonValue
    val value: String
) {
    PASSWORD("password")
}