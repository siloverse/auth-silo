package io.github.siloverse.auth.user.service

import io.github.siloverse.auth.error.DuplicateUserException
import io.github.siloverse.auth.error.UserAlreadyProvisionedException
import io.github.siloverse.auth.error.UserProvisioningFailedException
import io.github.siloverse.auth.user.mapper.UserMapper
import io.github.siloverse.auth.keycloak.service.KeycloakService
import io.github.siloverse.auth.web.request.RegistrationRequest
import io.github.siloverse.user.web.response.CreateUserResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import java.util.UUID

@Service
class UserService(
    private val keycloakService: KeycloakService,
    private val userMapper: UserMapper,
    private val userSiloService: UserSiloService
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun registerUser(request: RegistrationRequest): UUID {
        val userId = try {
            keycloakService.createUser(userMapper.toUserIdentity(request))
        } catch (_: DuplicateUserException) {
            logger.info("keycloak duplicate for {}; attempting orphan heal", request.email())
            val user = keycloakService.findUserIdByEmail(request.email())
            user.keycloakId ?: error("UserId is null for user email [${user.email}] after user creation.")
        }

        keycloakService.assignCustomerRole(userId)

        val resp = try {
            userSiloService.registerUser(userMapper.toCreateUserRequest(userId, request))
        } catch (e: UserAlreadyProvisionedException) {
            throw DuplicateUserException(request.email(), e)    // profile exists too → genuine duplicate → 409
        } catch (e: ResourceAccessException) {
            throw UserProvisioningFailedException(userId, e) // fix 1, unchanged
        } catch (e: HttpServerErrorException) {
            throw UserProvisioningFailedException(userId, e)
        }
        return resp.id
    }
}