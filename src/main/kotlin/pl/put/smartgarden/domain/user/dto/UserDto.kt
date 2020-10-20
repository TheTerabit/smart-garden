package pl.put.smartgarden.domain.user.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class UserSignUpDto(
        @field:Size(min = 3, max = 25, message = "{validation.user.username.min}")
        @field:Pattern(regexp = "^[a-zA-Z]+$", message = "{validation.user.username.letters}")
        val username: String,
        @field:Email(message = "{validation.user.email.not-valid}")
        @field:NotBlank(message = "{validation.user.email.not-empty}")
        val email: String,
        @field:NotBlank(message = "{validation.user.password.not-empty}")
        val password: String,
        val deviceGuid: String
)

data class UserSignInDto(
        val email: String,
        val password: String
)

data class UserSignInResponseDto(
        val token: String,
        val username: String,
        val id: String
)

data class UserResourceDto(
        val username: String,
        val email: String,
        val deviceGuid: String
)

data class UserChangePasswordDto(
        val oldPassword: String,
        val password: String,
        val passwordRepeated: String
)

data class UserChangeEmailDto(
        val password: String,
        val email: String
)

data class UserChangeUsernameDto(
        val password: String,
        val username: String
)