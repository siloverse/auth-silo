package io.github.siloverse.auth.user.controller

import io.github.siloverse.auth.error.DuplicateUserException
import io.github.siloverse.auth.keycloak.client.KeycloakClient
import io.github.siloverse.auth.user.service.UserService
import io.github.siloverse.auth.web.request.RegistrationRequest
import io.github.siloverse.auth.web.response.RegistrationResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registrations")
class RegistrationController(
    private val keycloak: KeycloakClient,
    private val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegistrationRequest): RegistrationResponse {
        val userId = userService.registerUser(request)
        return RegistrationResponse(userId)
    }

    @ExceptionHandler(DuplicateUserException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun duplicate(e: DuplicateUserException) = mapOf("error" to "email already registered")
}