package io.github.siloverse.auth.web.controller

import io.github.siloverse.auth.web.error.DuplicateUserException
import io.github.siloverse.auth.web.keycloak.KeycloakUserClient
import io.github.siloverse.auth.web.request.RegistrationRequest
import io.github.siloverse.auth.web.response.RegistrationResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registrations")
class RegistrationController(private val keycloak: KeycloakUserClient) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegistrationRequest): RegistrationResponse {
        val userId = keycloak.createUser(request.email, request.firstName, request.lastName, request.password)
        keycloak.assignCustomerRole(userId)
        return RegistrationResponse(userId)
    }

    @ExceptionHandler(DuplicateUserException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun duplicate(e: DuplicateUserException) = mapOf("error" to "email already registered")
}