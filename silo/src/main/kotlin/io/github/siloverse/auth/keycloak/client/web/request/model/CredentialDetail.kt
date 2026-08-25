package io.github.siloverse.auth.keycloak.client.web.request.model

data class CredentialDetail(
    val value: String,
    val type: CredentialType,
    val temporary: Boolean,
)