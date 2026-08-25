package io.github.siloverse.auth.user.mapper

import io.github.siloverse.auth.keycloak.model.CreateUserIdentity
import io.github.siloverse.auth.web.request.RegistrationRequest
import org.springframework.stereotype.Component

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
}