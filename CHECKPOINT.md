# CHECKPOINT — auth-silo

_Last updated: 2026-08-26. This document is the arbiter: if Claude asserts something about this repo that isn't here or visible in the code, call it — that's drift._

**Role in the platform:** registration orchestrator. Public `POST /api/registrations` (Phase 3) → creates the user in Keycloak (Admin API, client-credentials as its own service account, which holds realm-management `manage-users`) and in user-silo. Resource server for everything else. Base package `io.github.siloverse.auth`.

## Built & green (Phase 4.3 + 4.5, 2026-08-26) — user-silo mint call, failure honesty, orphan healing

- **Registration now spans both silos:** after Keycloak create + role assign, `UserSiloService` POSTs user-silo's `POST /api/users` (contract DTOs from user-silo `web` 1.1.3 — released, not SNAPSHOT) through a dedicated `userSiloClient` RestClient sharing the same SA wallet. Full ladder verified live: 201 → row in `user_silo.users` → new-user login → `/me` shows `ROLE_CUSTOMER`; duplicate → 409.
- **Failure honesty (4.5):** user-silo unreachable (`ResourceAccessException`) or 5xx (`HttpServerErrorException`) → `UserProvisioningFailedException(keycloakId)` → controller handler logs ERROR **naming the orphaned keycloakId** and returns **502** (not 500 — a dependency failed, auth-silo didn't; 500 pages the wrong on-call). Verified live: kill user-silo → 502 + orphan ERROR line.
- **Registration is idempotent — orphan healing (decision):** on Keycloak-duplicate, resolve the existing user id by email and CONTINUE the pipeline (re-assign role = no-op, call user-silo anyway). user-silo 201 → orphan healed, client gets 201; user-silo 409 (translated at the client boundary via `retrieve().onStatus(CONFLICT)` → `UserAlreadyProvisionedException`) → user fully exists → genuine 409. Verified live: manufactured orphan (user-silo down mid-registration) healed to 201 + row on retry; second retry 409; pre-existing full users still 409. Rationale over compensation (delete-on-failure): the delete can itself fail — compensation needs compensation; retry-heals is self-correcting. Each step is converge-to-desired-state, which is what makes the endpoint idempotent.
- Design shape: HTTP status codes are translated to domain exceptions at the client boundary (`onStatus` in `UserSiloService`); `UserService` reasons only in domain terms.

## Parked (Phase 4.3 review, 2026-08-26)

- **Interceptor doubled in `ClientConfiguration`:** `OAuth2ClientHttpRequestInterceptor(clientManager)` composed with the manual authorize+setBearerAuth lambda — the manual block overwrites the header, so the Spring interceptor is dead weight. Pick one (configure the Spring one with a clientRegistrationId resolver, or keep the lambda alone).
- `RegistrationController` injects `KeycloakClient` and never uses it — delete.
- Test users in realm + DB: ladder43/45, heal-t1/t5, orphan44 (all `@macgrant-platform.test`, synthetic per standing rule).
- Observed on registered humans: `ROLE_DEFAULT-ROLES-KYC` authority (Keycloak default composite). Inert under allowlist authorization; noted so it isn't rediscovered as a surprise.

## Built & green (Phase 3 core, 2026-08-21)

- **`POST /api/registrations` works end-to-end, verified:** anonymous request → validate (`@Valid`, 400) → create Keycloak user via Admin API (201, id parsed from `Location`) → assign realm role `customer` (second Admin call — the API ignores `realmRoles` on create) → login as the new user → user-silo `/me` shows `ROLE_CUSTOMER`. Duplicate email → 409 via `DuplicateUserException` handler. Decision: **username = email** (one identifier).
- **Outbound plumbing hand-built** (deliberately no keycloak-admin-client): Boot `oauth2-client` registration `keycloak-admin` (client_credentials; secret via `AUTH_SILO_CLIENT_SECRET` env var — nothing committed), `AuthorizedClientServiceOAuth2AuthorizedClientManager` (the request-bound default manager needs a servlet request — failed in ApplicationRunner with "servletRequest cannot be null"; service flavor is semantically right for app-as-itself calls), `RestClient` with an interceptor that asks the manager per request. Manager = token wallet: caches until expiry, refetches on demand.
- **Least privilege, learned by 403:** `manage-users` covers user CRUD + writing role mappings, but `GET /roles/{name}` (name→id resolution) needs `view-realm`. Granted exactly that, nothing broader.
- **Boot 4.1 observation:** client registration properties bind lazily — app starts fine without the secret; failure surfaces on first wallet use.

## Lessons paid for (Phase 3, 2026-08-21)

- **Tokens are snapshots, part 2:** after granting `view-realm`, the wallet's cached token still carried the old claims → continued 403 until restart/expiry. Fresh grants need fresh tokens.
- **The partial-failure window is real:** role-assign 403 after successful create left an orphaned roleless user — and the orphan made the retry fail with 409. Open design question (documented, not solved — feeds Phase 5's saga/outbox discussion): idempotent register (find-and-continue) vs compensation (delete on failure).
- **Two builders, one build/ dir = corrupted bookkeeping:** IDE and CLI Gradle alternating produced UP-TO-DATE-but-missing-class states and a poisoned build-cache entry. Rule: when UP-TO-DATE and ClassNotFound disagree, check the filesystem; `clean`; pick ONE builder per session.
- **An IDE package-move dragged the whole silo tree into `auth.web.*`** (the web module's contract namespace) — reverted; module boundary = package boundary. The moved-tests leftover broke compilation later (same-package tests lost sight of their subject).

## Built & green (Phase 2, 2026-08-20)

- Resource server against realm `kyc`: `oauth2-resource-server` via local catalog, `issuer-uri`, `SecurityConfiguration` + `KeycloakJwtAuthenticationConverter` — deliberate verbatim copies of user-silo's (copy #2 of 3; extraction to java-library is Phase 8.6, rule of three). Shared rationale + lessons: see user-silo/CHECKPOINT.md.
- Verified: no token → 401, user token → `ROLE_customer`, SA token → `ROLE_system`.

## Parked

- **Keycloak-down mapping (plan 3.3):** currently a raw 500; decide and implement 503 + no partial state (`ResourceAccessException`/5xx handler).
- **Registration tests** — the endpoint has no coverage yet (unit for KeycloakUserClient error paths, jwt()/anonymous rules for the permitAll).
- `web/build.gradle.kts` uses `implementation(spring-boot-starter-validation)`; honest scope for a contract module is `api(jakarta.validation-api)`.
- `/api/users/me` was added here for chain testing; semantically it belongs to user-silo — replace with the Phase 4.4 *relay* variant and delete the local copy.
- Next: auth-silo → user-silo synchronous call (Phase 4), token-fetch-count observation across two registrations (wallet caching Reflect) if not yet recorded.
