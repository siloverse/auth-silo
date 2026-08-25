package io.github.siloverse.auth.keycloak.mapper

import io.github.siloverse.auth.keycloak.client.web.request.model.CredentialDetail
import io.github.siloverse.auth.keycloak.client.web.request.model.CredentialType
import io.github.siloverse.auth.keycloak.client.web.request.model.UserRole
import io.github.siloverse.auth.keycloak.client.web.response.GetRoleResponse
import io.github.siloverse.auth.keycloak.model.CreateUserIdentity
import io.github.siloverse.auth.keycloak.client.web.request.CreateUserRequest
import org.springframework.stereotype.Component

@Component
class KeycloakMapper {

    fun toCreateUserRequest(dto: CreateUserIdentity): CreateUserRequest {
        return CreateUserRequest(
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            credentials = listOf(
                CredentialDetail(
                    value = dto.password,
                    type = CredentialType.PASSWORD,
                    temporary = false
                )
            ),
            username = dto.email,
            enabled = true
        )
    }

    fun toUserRole(resp: GetRoleResponse): UserRole {
        return UserRole(
            id = resp.id,
            name = resp.name
        )
    }
}