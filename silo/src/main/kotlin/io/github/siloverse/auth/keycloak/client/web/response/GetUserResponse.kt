package io.github.siloverse.auth.keycloak.client.web.response

import java.util.UUID

data class GetUserResponse(
    val id: UUID,
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
)