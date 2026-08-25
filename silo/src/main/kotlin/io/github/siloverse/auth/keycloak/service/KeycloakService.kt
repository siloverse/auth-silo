package io.github.siloverse.auth.keycloak.service

import io.github.siloverse.auth.keycloak.client.KeycloakClient
import io.github.siloverse.auth.keycloak.model.CreateUserIdentity
import io.github.siloverse.auth.keycloak.model.enum.Role
import io.github.siloverse.auth.keycloak.mapper.KeycloakMapper
import org.springframework.stereotype.Service

@Service
class KeycloakService(
    private val keycloakClient: KeycloakClient,
    private val keycloakMapper: KeycloakMapper
) {
    fun createUser(createUserIdentity: CreateUserIdentity): String {
        val req = keycloakMapper.toCreateUserRequest(createUserIdentity)
        return keycloakClient.createUser(req)
    }

    fun assignCustomerRole(userId: String) {
        val resp = keycloakClient.getRoleByName(Role.CUSTOMER.value)
        val userRoles = listOf(keycloakMapper.toUserRole(resp))
        keycloakClient.assignUserRole(userId, userRoles)
    }
}