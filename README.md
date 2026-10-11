# auth-silo

Registration orchestrator for the siloverse platform. Spring Boot 4 on Kotlin, Java 21.
Listens on port `10001` and is reachable at `https://auth-silo.macgrant-platform.test`
through the macgrant gateway.

## What it does

`POST /api/registrations` (public) runs the registration pipeline:

1. Validate the request (`@Valid`, 400 on failure). Username is the email.
2. Create the user in Keycloak realm `kyc` through the Admin API, authenticating as its
   own service account (client credentials; realm-management grants `manage-users` and
   `view-realm`, nothing broader).
3. Assign realm role `customer` (a second Admin call; create ignores `realmRoles`).
4. Call user-silo `POST /api/users` with the same service token so the user exists in
   the system of record.

The pipeline is idempotent and orphan-healing: a Keycloak duplicate resumes from the
existing user, user-silo 409 means the user fully exists and answers 409, user-silo
unreachable or 5xx answers 502 with an ERROR log naming the orphaned `keycloakId`.

Everything else is a resource server against realm `kyc`.

## Modules

| Module | Contents |
| --- | --- |
| `silo` | the runnable service: security, Keycloak and user-silo clients, registration |
| `web` | API contract DTOs |
| `messages` | reserved for this silo's events (none yet) |
| `ui` | placeholder |

## Outbound clients

Hand-built on purpose, no `keycloak-admin-client`: Boot's `oauth2-client` registration,
an `AuthorizedClientServiceOAuth2AuthorizedClientManager` acting as the token wallet, and
`RestClient` builders carrying `OAuth2ClientHttpRequestInterceptor`. The injected
`RestClient.Builder` keeps the clients inside Boot's tracing instrumentation.

The client secret in `application.yml` is the disposable local development secret that
macgrant provisions for realm `kyc`; it is committed by decision until Vault arrives
(plan task 8.3).

## Observability

Traces and logs go to the macgrant OpenTelemetry collector over OTLP; metrics are
scraped by the platform's Prometheus from `/actuator/prometheus`. One trace spans this
silo, user-silo and notification-silo's consumer.

## Running

1. Bring the platform up (`vagrant up` in macgrant).
2. `./gradlew :silo:bootRun`.
3. Tests: `./gradlew build`. Security tests use a test-only claims-probe controller.

## Dependencies

- user-silo `web` contract jar, pinned to `1.1.4-SNAPSHOT` from `mavenLocal()` until
  user-silo 1.1.4 is released.
- Build conventions from siloverse-build 1.11.1.

`CHECKPOINT.md` is the engineering journal and the arbiter of what is decided and built.
