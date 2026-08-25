package io.github.siloverse.auth.user.service

import io.github.siloverse.auth.user.mapper.UserMapper
import io.github.siloverse.auth.keycloak.service.KeycloakService
import io.github.siloverse.auth.web.request.RegistrationRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val keycloakService: KeycloakService,
    private val userMapper: UserMapper,
    private val userSiloService: UserSiloService
) {

    fun registerUser(request: RegistrationRequest): UUID {
        val userId = keycloakService.createUser(userMapper.toCreateUserIdentity(request))
        keycloakService.assignCustomerRole(userId)
        val resp = userSiloService.registerUser(userMapper.toCreateUserRequest(userId, request))
        return resp.id
    }
}