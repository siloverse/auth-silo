package io.github.siloverse.auth.error

class DuplicateUserException(
    email: String,
    throwable: Throwable
) : RuntimeException("user with this email [$email] already exists", throwable)