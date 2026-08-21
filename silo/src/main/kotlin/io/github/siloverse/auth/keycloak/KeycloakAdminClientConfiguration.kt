package io.github.siloverse.auth.keycloak

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.web.client.RestClient

@Configuration
class KeycloakAdminClientConfiguration {

    @Bean
    fun keycloakAdminRestClient(
        clientManager: OAuth2AuthorizedClientManager,
        @Value("\${keycloak.admin-base-url}") baseUrl: String,
    ): RestClient = RestClient.builder()
        .baseUrl(baseUrl)
        .requestInterceptor { request, body, execution ->
            val authorized = clientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId("auth-silo")
                    .principal("auth-silo")
                    .build()
            ) ?: error("could not obtain client-credentials token for 'auth-silo'")
            request.headers.setBearerAuth(authorized.accessToken.tokenValue)
            execution.execute(request, body)
        }
        .build()

    @Bean
    fun authorizedClientManager(
        registrations: ClientRegistrationRepository,
        clientService: OAuth2AuthorizedClientService,
    ): OAuth2AuthorizedClientManager =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, clientService).apply {
            setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder()
                    .clientCredentials()
                    .build()
            )
        }
}