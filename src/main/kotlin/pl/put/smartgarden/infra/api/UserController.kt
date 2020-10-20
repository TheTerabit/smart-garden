package pl.put.smartgarden.infra.api

import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.*
import javax.validation.Valid

@Api(description = "Users api")
@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @PostMapping("/sign-up")
    @ApiOperation("Create new user account.")
    @ApiResponses(value = [
        ApiResponse(code = 204, message = "No Content"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signUp(@Valid @RequestBody user: UserSignUpDto) {
        userService.signUpUser(user)
    }

    @PostMapping("/sign-up/confirmation")
    @ApiOperation("Confirm account creation (link sent to email).")
    @ApiResponses(value = [
        ApiResponse(code = 204, message = "No Content"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ApiParam(value = "token", required = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signUpConfirmation(@RequestParam token : String) {

    }

    @PostMapping("/sign-in")
    @ApiOperation("Sign in and get JWT.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun signIn(@RequestBody user: UserSignInDto) : UserSignInResponseDto{
        return UserSignInResponseDto("jgdsgfgfgdfgadsfgergre325r5y4hbdzfs.b3twfersv", "nazwauzytkownika", "124324edfef324")
    }

    @GetMapping
    @ApiOperation("Get all users.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getUsers(): List<UserResourceDto> = userService.getUsers()

    @GetMapping("/{id}")
    @ApiOperation("Get one user details.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getUser(@PathVariable id : String) : UserResourceDto {
        return UserResourceDto("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
    }

    @GetMapping("/me")
    @ApiOperation("Get currently logged user details.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentUser() : UserResourceDto {
        return UserResourceDto("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
    }

    @PutMapping("/me/change-password")
    @ApiOperation("Change currently logged user password.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changePassword(@RequestBody user: UserChangePasswordDto) : UserResourceDto{
        return UserResourceDto("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
    }

    @PutMapping("/me/change-email")
    @ApiOperation("Change currently logged user email.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeEmail(@RequestBody user: UserChangeEmailDto) : UserResourceDto {
        return UserResourceDto("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")

    }

    @PutMapping("/me/change-username")
    @ApiOperation("Change currently logged user username.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeUsername(@RequestBody user: UserChangeUsernameDto) : UserResourceDto {
        return UserResourceDto("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
    }
}