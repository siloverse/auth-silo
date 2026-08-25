package io.github.siloverse.auth.user.mapper

import io.github.siloverse.auth.keycloak.model.CreateUserIdentity
import io.github.siloverse.auth.web.request.RegistrationRequest
import io.github.siloverse.user.web.request.CreateUserRequest
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserMapper {

    fun toCreateUserIdentity(request: RegistrationRequest): CreateUserIdentity {
        return CreateUserIdentity(
            email = request.email(),
            firstName = request.firstName(),
            lastName = request.lastName(),
            password = request.password()
        )
    }

    fun toCreateUserRequest(userId: String, request: RegistrationRequest): CreateUserRequest {
        return CreateUserRequest(
            email = request.email(),
            keycloakId = UUID.fromString(userId),
            displayName = "${request.firstName} ${request.lastName}".trim()
        )
    }
}