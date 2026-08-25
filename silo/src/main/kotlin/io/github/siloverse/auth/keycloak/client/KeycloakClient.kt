package io.github.siloverse.auth.keycloak.client

import io.github.siloverse.auth.error.DuplicateUserException
import io.github.siloverse.auth.keycloak.client.web.request.model.UserRole
import io.github.siloverse.auth.keycloak.client.web.response.GetRoleResponse
import io.github.siloverse.auth.keycloak.client.web.request.CreateUserRequest
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class KeycloakClient(private val keycloakAdminRestClient: RestClient) {

    fun createUser(createUserRequest: CreateUserRequest): String {
        val response = try {
            keycloakAdminRestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createUserRequest)
                .retrieve()
                .toBodilessEntity()
        } catch (e: HttpClientErrorException.Conflict) {
            throw DuplicateUserException(createUserRequest.email, e)
        }
        val location = response.headers.location ?: error("Keycloak returned 201 without a Location header")
        return location.path.substringAfterLast('/')
    }


    fun getRoleByName(roleName: String): GetRoleResponse {
        return keycloakAdminRestClient.get().uri("/roles/{roleName}", roleName)
            .retrieve().body() ?: error("realm role 'customer' not found")
    }

    fun assignUserRole(userId: String, userRoles: List<UserRole>) {
        keycloakAdminRestClient
            .post()
            .uri("/users/{id}/role-mappings/realm", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(userRoles)
            .retrieve()
            .toBodilessEntity()
    }
}