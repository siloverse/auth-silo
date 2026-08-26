package io.github.siloverse.auth.keycloak.service

import io.github.siloverse.auth.keycloak.client.KeycloakClient
import io.github.siloverse.auth.keycloak.model.UserIdentity
import io.github.siloverse.auth.keycloak.model.enum.Role
import io.github.siloverse.auth.keycloak.mapper.KeycloakMapper
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class KeycloakService(
    private val keycloakClient: KeycloakClient,
    private val keycloakMapper: KeycloakMapper
) {
    fun createUser(userIdentity: UserIdentity): UUID {
        val req = keycloakMapper.toCreateUserRequest(userIdentity)
        val userId = keycloakClient.createUser(req)
        return UUID.fromString(userId)
    }

    fun assignCustomerRole(userId: UUID) {
        val resp = keycloakClient.getRoleByName(Role.CUSTOMER.value)
        val userRoles = listOf(keycloakMapper.toUserRole(resp))
        keycloakClient.assignUserRole(userId, userRoles)
    }

    fun findUserIdByEmail(email: String): UserIdentity {
        val resp = keycloakClient.getUserByEmail(email) ?: error("User with $email is not found on keycloak.")
        return keycloakMapper.toUserIdentity(resp)
    }
}