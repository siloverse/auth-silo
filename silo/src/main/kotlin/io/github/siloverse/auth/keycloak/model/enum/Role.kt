package io.github.siloverse.auth.keycloak.model.enum

enum class Role(val value: String) {
    SYSTEM("system"),
    EMPLOYEE("employee"),
    CUSTOMER("customer")
}