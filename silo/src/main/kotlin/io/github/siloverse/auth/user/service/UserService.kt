package io.github.siloverse.auth.user.service

import io.github.siloverse.auth.user.mapper.UserMapper
import io.github.siloverse.auth.keycloak.service.KeycloakService
import io.github.siloverse.auth.web.request.RegistrationRequest
import org.springframework.stereotype.Service

@Service
class UserService(
    private val keycloakService: KeycloakService,
    private val userMapper: UserMapper
) {

    fun registerUser(request: RegistrationRequest): String {
        val userId = keycloakService.createUser(userMapper.toCreateUserIdentity(request))
        keycloakService.assignCustomerRole(userId);
        return userId;
    }
}