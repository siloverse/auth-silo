package io.github.siloverse.auth.error

import java.util.UUID

class UserAlreadyProvisionedException(val keycloakId: UUID) :
    RuntimeException("user-silo already has a profile for keycloakId=$keycloakId")