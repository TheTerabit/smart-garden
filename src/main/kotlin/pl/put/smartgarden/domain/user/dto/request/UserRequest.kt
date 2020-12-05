package pl.put.smartgarden.domain.user.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class UserSignUpRequest(
    @field:NotBlank(message = "{validation.user.username.not-empty}")
    val username: String,
    @field:Email(message = "{validation.user.email.not-valid}")
    @field:NotBlank(message = "{validation.user.email.not-empty}")
    val email: String,
    @field:NotBlank(message = "{validation.user.password.not-empty}")
    val password: String,
    @field:NotBlank(message = "{validation.user.deviceGuid.not-empty}")
    val deviceGuid: String,
    @field:NotNull(message = "{validation.user.location.not-empty}")
    val longitude: Double,
    @field:NotNull(message = "{validation.user.location.not-empty}")
    val latitude: Double
)

data class UserSignInRequest(
    val email: String,
    val password: String
)

data class UserSignInResponse(
    val token: String,
    val username: String
)

data class UserChangePasswordRequest(
    val oldPassword: String,
    val password: String,
    val passwordRepeated: String
)

data class UserChangeEmailRequest(
    val password: String,
    val email: String
)

data class UserChangeUsernameRequest(
    val password: String,
    val username: String
)