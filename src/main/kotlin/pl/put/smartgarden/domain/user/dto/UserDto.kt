package pl.put.smartgarden.domain.user.dto

import javax.validation.constraints.*

data class UserSignUpDto(
        @field:Min(value=3, message = "{validation.user.username.min}")
        @field:Max(value=25, message = "{validation.user.username.max}")
        @field:Pattern(regexp = "[a-zA-Z]", message = "{validation.user.username.letters}")
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
        val token : String,
        val username : String,
        val id : String
)

data class UserResourceDto(
        val username: String,
        val email: String,
        val deviceGuid: String
)

data class UserChangePasswordDto(
        val oldPassword : String,
        val password : String,
        val passwordRepeated : String
)

data class UserChangeEmailDto(
        val password : String,
        val email : String
)

data class UserChangeUsernameDto(
        val password : String,
        val username : String
)