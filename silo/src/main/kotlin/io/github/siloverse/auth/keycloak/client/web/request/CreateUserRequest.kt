package io.github.siloverse.auth.keycloak.client.web.request

import io.github.siloverse.auth.keycloak.client.web.request.model.CredentialDetail

data class CreateUserRequest(
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val credentials: List<CredentialDetail>,
    val enabled: Boolean = true,
)
