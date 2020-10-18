package pl.put.smartgarden.domain.user.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserSignUpDto(
        @field:NotBlank(message = "{validation.user.username.not-empty}")
        val username: String,
        @field:Email(message = "{validation.user.email.not-valid}")
        @field:NotBlank(message = "{validation.user.email.not-empty}")
        val email: String,
        @field:NotBlank(message = "{validation.user.password.not-empty}")
        val password: String
)

data class UserSignInDto(
        val email: String,
        val password: String
)