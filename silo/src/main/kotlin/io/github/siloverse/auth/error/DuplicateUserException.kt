package io.github.siloverse.auth.web.error

class DuplicateUserException(email: String) : RuntimeException("user with this email [$email] already exists")