package io.github.siloverse.auth.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // application-test.yml: no network at context start
class SecurityConfigurationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    /**
     * auth-silo's only production endpoint (POST /api/registrations) is deliberately public, so
     * the resource-server wiring is proven against this test-only probe: any protected URL runs
     * the same filter chain and Keycloak converter that will guard future endpoints. The nested
     * {@code @TestConfiguration} is auto-detected by {@code @SpringBootTest}, and Spring registers
     * its component-annotated member class as a bean -- no {@code @Bean} method (that would
     * register the controller twice: ambiguous mapping).
     */
    @TestConfiguration
    class ClaimsProbe {

        @RestController
        class ClaimsProbeController {
            @GetMapping("/api/claims-probe")
            fun claims(authentication: JwtAuthenticationToken) = mapOf(
                "username" to authentication.token.getClaimAsString("preferred_username"),
                "authorities" to authentication.authorities.map { it.authority },
            )
        }
    }

    @Test
    fun `anonymous request is rejected with 401`() {
        mockMvc.get("/api/claims-probe").andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `authenticated request reaches the endpoint and sees its claims`() {
        mockMvc.get("/api/claims-probe") {
            with(
                jwt()
                    .jwt { it.subject("some-uuid").claim("preferred_username", "test_user") }
                    .authorities(SimpleGrantedAuthority("ROLE_CUSTOMER")))
        }.andExpect {
            status { isOk() }
            jsonPath("$.username") { value("test_user") }
            jsonPath("$.authorities[0]") { value("ROLE_CUSTOMER") }
        }
    }

    @Test
    fun `hello endpoint also requires authentication`() {
        mockMvc.get("/").andExpect { status { isUnauthorized() } }
    }
}
