package pl.put.smartgarden.domain.user.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class UserSignUpRequest(
    @field:Size(min = 3, max = 25, message = "{validation.user.username.min}")
    @field:Pattern(regexp = "^[a-zA-Z]+$", message = "{validation.user.username.letters}")
    val username: String,
    @field:Email(message = "{validation.user.email.not-valid}")
    @field:NotBlank(message = "{validation.user.email.not-empty}")
    val email: String,
    @field:NotBlank(message = "{validation.user.password.not-empty}")
    val password: String,
    @field:NotBlank(message = "{validation.user.device.not-empty}")
    val deviceGuid: String,
    @field:NotBlank(message = "{validation.user.location.not-empty}")
    val longitude: Double,
    @field:NotBlank(message = "{validation.user.location.not-empty}")
    val latitude: Double
)

data class UserSignInRequest(
    val email: String,
    val password: String
)

data class UserSignInResponse(
    val token: String,
    val username: String,
    val id: Int
)

data class UserResourceResponse(
    val id: Int,
    val username: String,
    val email: String,
    val deviceGuid: String?
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