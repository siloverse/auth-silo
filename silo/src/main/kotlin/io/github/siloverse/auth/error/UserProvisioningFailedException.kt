package io.github.siloverse.auth.error

import java.util.UUID

class UserProvisioningFailedException(
    val keycloakId: UUID,
    cause: Throwable,
) : RuntimeException("user-silo call failed; Keycloak user $keycloakId is now orphaned", cause)