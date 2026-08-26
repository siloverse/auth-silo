package io.github.siloverse.auth.config

import io.github.siloverse.auth.config.settings.KeycloakClientSettings
import io.github.siloverse.auth.config.settings.UserSiloClientSettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.client.RestClient

@Configuration
class ClientConfiguration {

    @Bean
    fun keycloakAdminRestClient(
        keycloakClientSettings: KeycloakClientSettings,
        clientManager: OAuth2AuthorizedClientManager
    ): RestClient {
        return RestClient.builder()
            .baseUrl(keycloakClientSettings.baseUrl)
            .requestInterceptor(oauth2ClientCredentialsInterceptor(clientManager))
            .build()
    }


    @Bean
    fun userSiloClient(
        userSiloClientSettings: UserSiloClientSettings,
        clientManager: OAuth2AuthorizedClientManager
    ): RestClient {
        return RestClient.builder()
            .baseUrl(userSiloClientSettings.baseUrl)
            .requestInterceptor(oauth2ClientCredentialsInterceptor(clientManager))
            .build()
    }


    private fun oauth2ClientCredentialsInterceptor(
        clientManager: OAuth2AuthorizedClientManager
    ): ClientHttpRequestInterceptor {
        return ClientHttpRequestInterceptor { request, body, execution ->
            val authorized = clientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId("auth-silo")
                    .principal("auth-silo")
                    .build()
            ) ?: error("could not obtain client-credentials token for 'auth-silo'")
            request.headers.setBearerAuth(authorized.accessToken.tokenValue)
            execution.execute(request, body)
        }
    }
}
