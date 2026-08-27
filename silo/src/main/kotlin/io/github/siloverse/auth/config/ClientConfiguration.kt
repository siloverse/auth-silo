package io.github.siloverse.auth.config

import io.github.siloverse.auth.config.settings.KeycloakClientSettings
import io.github.siloverse.auth.config.settings.UserSiloClientSettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient

@Configuration
class ClientConfiguration {

    @Bean
    fun keycloakAdminRestClient(
        builder: RestClient.Builder,
        keycloakClientSettings: KeycloakClientSettings,
        clientManager: OAuth2AuthorizedClientManager,
    ): RestClient {
        return builder.baseUrl(keycloakClientSettings.baseUrl)
            .requestInterceptor(oauth2ClientCredentialsInterceptor(clientManager))
            .build()
    }


    @Bean
    fun userSiloClient(
        builder: RestClient.Builder,
        userSiloClientSettings: UserSiloClientSettings,
        clientManager: OAuth2AuthorizedClientManager
    ): RestClient {
        return builder.baseUrl(userSiloClientSettings.baseUrl)
            .requestInterceptor(oauth2ClientCredentialsInterceptor(clientManager))
            .build()
    }


    private fun oauth2ClientCredentialsInterceptor(
        clientManager: OAuth2AuthorizedClientManager,
    ): ClientHttpRequestInterceptor {
        return OAuth2ClientHttpRequestInterceptor(clientManager).apply {
            setClientRegistrationIdResolver {
                "auth-silo"
            }
        }
    }
}
