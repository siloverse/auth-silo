package io.github.siloverse.auth.user.service

import io.github.siloverse.user.web.request.CreateUserRequest
import io.github.siloverse.user.web.response.CreateUserResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Service
class UserSiloService(
    private val userSiloClient: RestClient,
) {

    fun registerUser(request: CreateUserRequest): CreateUserResponse {
        return userSiloClient.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body<CreateUserResponse>()!!
    }
}