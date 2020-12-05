package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.user.UserDeviceService
import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.dto.request.UserChangeEmailRequest
import pl.put.smartgarden.domain.user.dto.request.UserChangePasswordRequest
import pl.put.smartgarden.domain.user.dto.request.UserChangeUsernameRequest
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import javax.servlet.http.HttpServletRequest

@Api(description = "User general settings API")
@RestController
@RequestMapping("/users/me")
class UserGeneralSettingsController(
    val userService: UserService,
    val userDeviceService: UserDeviceService
) {
    @GetMapping()
    @ApiOperation("Get currently logged user details.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentUser(@ApiParam(hidden = true) @RequestAttribute("userId") userId: Int): UserGeneralSettingsResponse {
        return userService.getUserGeneralSettings(userId)
    }

    @PutMapping("/change-password")
    @ApiOperation("Change currently logged user password.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changePassword(@RequestBody user: UserChangePasswordRequest): UserGeneralSettingsResponse {
        // TODO
        return UserGeneralSettingsResponse("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123", 12.3, 12.3)
    }

    @PutMapping("/change-email")
    @ApiOperation("Change currently logged user email.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeEmail(@RequestBody user: UserChangeEmailRequest): UserGeneralSettingsResponse {
        // TODO
        return UserGeneralSettingsResponse("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123", 11.2, 12.3)
    }

    @PutMapping("/change-username")
    @ApiOperation("Change currently logged user username.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeUsername(@RequestBody user: UserChangeUsernameRequest): UserGeneralSettingsResponse {
        // TODO
        return UserGeneralSettingsResponse("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123", 111.2, 53.1)
    }

    @PutMapping("/location")
    @ApiOperation("Set location of user's device.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeLocation(
        @ApiParam(hidden = true) @RequestAttribute("userId") userId: Int,
        @RequestBody locationRequest: LocationRequest
    ): UserGeneralSettingsResponse =
        userDeviceService.setDeviceLocation(userId, locationRequest)
}