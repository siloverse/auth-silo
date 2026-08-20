# CHECKPOINT — auth-silo

_Last updated: 2026-08-20. This document is the arbiter: if Claude asserts something about this repo that isn't here or visible in the code, call it — that's drift._

**Role in the platform:** registration orchestrator. Public `POST /api/registrations` (Phase 3) → creates the user in Keycloak (Admin API, client-credentials as its own service account, which holds realm-management `manage-users`) and in user-silo. Resource server for everything else. Base package `io.github.siloverse.auth`.

## Built & green (Phase 2, 2026-08-20)

- Resource server against realm `kyc`: `oauth2-resource-server` via local catalog, `issuer-uri`, `SecurityConfiguration` + `KeycloakJwtAuthenticationConverter` — deliberate verbatim copies of user-silo's (copy #2 of 3; extraction to java-library is Phase 8.6, rule of three). Shared rationale + lessons: see user-silo/CHECKPOINT.md.
- Verified: no token → 401, user token → `ROLE_customer`, SA token → `ROLE_system`.

## Parked

- `/api/users/me` was added here for chain testing; semantically it belongs to user-silo — replace with the Phase 4.4 *relay* variant (auth-silo forwards the caller's token to user-silo) and delete the local copy.
- `permitAll` for `POST /api/registrations` — added in Phase 3 with the endpoint itself.
- Tests (plan 2.6).
