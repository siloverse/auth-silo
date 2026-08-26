package io.github.siloverse.auth.user.mapper

import io.github.siloverse.auth.keycloak.model.UserIdentity
import io.github.siloverse.auth.web.request.RegistrationRequest
import io.github.siloverse.user.web.request.CreateUserRequest
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserMapper {

    fun toUserIdentity(request: RegistrationRequest): UserIdentity {
        return UserIdentity(
            email = request.email(),
            firstName = request.firstName(),
            lastName = request.lastName(),
            password = request.password()
        )
    }

    fun toCreateUserRequest(userId: UUID, request: RegistrationRequest): CreateUserRequest {
        return CreateUserRequest(
            email = request.email(),
            keycloakId = userId,
            displayName = "${request.firstName} ${request.lastName}".trim()
        )
    }
}