package io.github.siloverse.auth.config

import io.github.siloverse.auth.security.KeycloakJwtAuthenticationConverter
import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                it.requestMatchers(HttpMethod.POST, "/api/registrations").permitAll()
                it.requestMatchers(
                    "/actuator/health",
                    "/actuator/prometheus",
                    "/actuator/info"
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.jwt { rs -> rs.jwtAuthenticationConverter(KeycloakJwtAuthenticationConverter()) }
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .build()
    }

    @Bean
    fun authorizedClientManager(
        registrations: ClientRegistrationRepository,
        clientService: OAuth2AuthorizedClientService,
    ): OAuth2AuthorizedClientManager {
        return AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, clientService).apply {
            setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder()
                    .clientCredentials()
                    .build()
            )
        }
    }
}