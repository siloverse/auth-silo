package io.github.siloverse.auth.config

import io.github.siloverse.auth.config.settings.KeycloakClientSettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import java.util.function.Consumer

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


    /**
     * The user-silo client comes generated from user-silo's OpenAPI contract and configures its
     * own RestClient from `clients.user-silo.url`. This hook adds what only the consumer knows:
     * the client-credentials wallet.
     */
    @Bean("user-silo-customizer")
    fun userSiloCustomizer(clientManager: OAuth2AuthorizedClientManager): Consumer<RestClient.Builder> =
        Consumer { builder -> builder.requestInterceptor(oauth2ClientCredentialsInterceptor(clientManager)) }

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
