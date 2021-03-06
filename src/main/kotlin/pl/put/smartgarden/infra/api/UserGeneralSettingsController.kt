package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
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

@Api(description = "User general settings API")
@CrossOrigin
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
    fun getCurrentUser(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int): UserGeneralSettingsResponse {
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
    fun changePassword(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
                       @RequestBody userRequest: UserChangePasswordRequest): UserGeneralSettingsResponse {
        return userService.changePassword(userId, userRequest)
    }

    @PutMapping("/change-email")
    @ApiOperation("NOT IMPLEMENTED Change currently logged user email.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeEmail(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
                    @RequestBody userRequest: UserChangeEmailRequest): UserGeneralSettingsResponse {
        return userService.changeEmail(userId, userRequest)
    }

    @PutMapping("/change-username")
    @ApiOperation("NOT IMPLEMENTED Change currently logged user username.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeUsername(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
                       @RequestBody user: UserChangeUsernameRequest): UserGeneralSettingsResponse {
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
    fun changeLocation(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
                       @RequestBody locationRequest: LocationRequest): UserGeneralSettingsResponse =
        userDeviceService.setDeviceLocation(userId, locationRequest)
}