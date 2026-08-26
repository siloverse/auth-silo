package io.github.siloverse.auth.user.controller

import io.github.siloverse.auth.error.DuplicateUserException
import io.github.siloverse.auth.error.UserProvisioningFailedException
import io.github.siloverse.auth.user.service.UserService
import io.github.siloverse.auth.web.request.RegistrationRequest
import io.github.siloverse.auth.web.response.RegistrationResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/registrations")
class RegistrationController(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(RegistrationController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegistrationRequest): RegistrationResponse {
        val userId = userService.registerUser(request)
        return RegistrationResponse(userId)
    }

    @ExceptionHandler(DuplicateUserException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun duplicate(e: DuplicateUserException) = mapOf("error" to "email already registered")

    @ExceptionHandler(UserProvisioningFailedException::class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    fun provisioningFailed(e: UserProvisioningFailedException): Map<String, String> {
        logger.error("registration half-completed: keycloakId={} is orphaned in Keycloak", e.keycloakId, e)
        return mapOf("error" to "registration could not be completed, please retry")
    }
}