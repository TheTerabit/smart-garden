package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import javax.validation.Valid

@Api(description = "Users authorization api")
@CrossOrigin
@RestController
@RequestMapping("/users")
class UserAuthController(
    private val userService: UserService
) {
    @PostMapping
    @ApiOperation("Create new user account.")
    @ApiResponses(value = [
        ApiResponse(code = 204, message = "No Content"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 409, message = "Conflict")
    ])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signUp(@Valid @RequestBody user: UserSignUpRequest) = userService.signUpUser(user)

    @GetMapping("/sign-up-confirmation")
    @ApiOperation("Confirm account creation (link sent to email).")
    @ApiResponses(value = [
        ApiResponse(code = 204, message = "No Content"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ApiParam(value = "token", required = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signUpConfirmation(@RequestParam token: String) = userService.enableUserIfValid(token)

    @PutMapping("/login")
    @ApiOperation("Sign in and get JWT.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun signIn(@RequestBody user: UserSignInRequest): UserSignInResponse = userService.signIn(user)

    @PostMapping("/logout")
    @ApiOperation("Sign out and revoke JWT.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun signOut(@ApiParam(hidden = true) @RequestAttribute("token") token: String) = userService.signOut(token)
}