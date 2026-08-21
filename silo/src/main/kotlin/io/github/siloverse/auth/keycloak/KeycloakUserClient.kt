package io.github.siloverse.auth.web.keycloak

import io.github.siloverse.auth.web.error.DuplicateUserException
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import kotlin.collections.get

@Component
class KeycloakUserClient(private val keycloakAdminRestClient: RestClient) {

    fun createUser(email: String, firstName: String, lastName: String, password: String): String {
        val response = try {
            keycloakAdminRestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    mapOf(
                        "username" to email,
                        "email" to email,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "enabled" to true,
                        "credentials" to listOf(mapOf("type" to "password", "value" to password, "temporary" to false)),
                    )
                )
                .retrieve()
                .toBodilessEntity()
        } catch (e: HttpClientErrorException.Conflict) {
            throw DuplicateUserException(email)
        }
        val location = response.headers.location ?: error("Keycloak returned 201 without a Location header")
        return location.path.substringAfterLast('/')
    }

    fun assignCustomerRole(userId: String) {
        val role = keycloakAdminRestClient.get().uri("/roles/customer")
            .retrieve().body(Map::class.java) ?: error("realm role 'customer' not found")
        keycloakAdminRestClient.post().uri("/users/{id}/role-mappings/realm", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .body(listOf(mapOf("id" to role["id"], "name" to role["name"])))
            .retrieve().toBodilessEntity()
    }
}