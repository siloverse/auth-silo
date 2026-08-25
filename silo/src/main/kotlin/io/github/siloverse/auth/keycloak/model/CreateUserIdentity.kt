package io.github.siloverse.auth.keycloak.model

data class CreateUserIdentity(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)