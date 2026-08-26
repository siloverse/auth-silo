package io.github.siloverse.auth.keycloak.mapper

import io.github.siloverse.auth.keycloak.client.web.request.model.CredentialDetail
import io.github.siloverse.auth.keycloak.client.web.request.model.CredentialType
import io.github.siloverse.auth.keycloak.client.web.request.model.UserRole
import io.github.siloverse.auth.keycloak.client.web.response.GetRoleResponse
import io.github.siloverse.auth.keycloak.model.UserIdentity
import io.github.siloverse.auth.keycloak.client.web.request.CreateUserRequest
import io.github.siloverse.auth.keycloak.client.web.response.GetUserResponse
import org.springframework.stereotype.Component

@Component
class KeycloakMapper {

    fun toCreateUserRequest(userIdentity: UserIdentity): CreateUserRequest {

        require(!userIdentity.password.isNullOrBlank()) {
            "Email must not be null, empty, or blank"
        }

        return CreateUserRequest(
            email = userIdentity.email,
            firstName = userIdentity.firstName,
            lastName = userIdentity.lastName,
            credentials = listOf(
                CredentialDetail(
                    value = userIdentity.password,
                    type = CredentialType.PASSWORD,
                    temporary = false
                )
            ),
            username = userIdentity.email,
            enabled = true
        )
    }

    fun toUserIdentity(response: GetUserResponse): UserIdentity {
        return UserIdentity(
            keycloakId = response.id,
            email = response.email,
            firstName = response.firstName,
            lastName = response.lastName,
        )
    }

    fun toUserRole(resp: GetRoleResponse): UserRole {
        return UserRole(
            id = resp.id,
            name = resp.name
        )
    }
}