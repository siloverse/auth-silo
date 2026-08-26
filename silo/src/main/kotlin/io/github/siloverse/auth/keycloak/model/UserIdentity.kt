package io.github.siloverse.auth.keycloak.model

import java.util.UUID

data class UserIdentity(
    val keycloakId: UUID? = null,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val password: String? = null
)