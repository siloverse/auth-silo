package io.github.siloverse.auth.user.service

import io.github.siloverse.auth.error.UserAlreadyProvisionedException
import io.github.siloverse.user.client.api.UserControllerApi
import io.github.siloverse.user.client.model.CreateUserRequest
import io.github.siloverse.user.client.model.CreateUserResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException

/**
 * Talks to user-silo through its generated client: the request and response types, the path
 * and the HTTP method come from user-silo's committed OpenAPI contract, not from code written here.
 */
@Service
class UserSiloService(
    private val userControllerApi: UserControllerApi,
) {

    fun registerUser(request: CreateUserRequest): CreateUserResponse =
        try {
            userControllerApi.create(request)
        } catch (e: HttpClientErrorException.Conflict) {
            throw UserAlreadyProvisionedException(request.keycloakId)
        }
}
