package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.request.AreaRequest
import pl.put.smartgarden.domain.user.dto.request.AreaSettingsRequest
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.dto.request.UserChangeEmailRequest
import pl.put.smartgarden.domain.user.dto.request.UserChangePasswordRequest
import pl.put.smartgarden.domain.user.dto.request.UserChangeUsernameRequest
import pl.put.smartgarden.domain.user.dto.request.UserResourceResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import java.time.Instant
import javax.validation.Valid

@Api(description = "Users api")
@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

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
    fun signOut(@RequestHeader("Authorization") token: String) = userService.signOut(token)

    @GetMapping
    @ApiOperation("Get all users.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getUsers(): List<UserResourceResponse> = userService.getUsers()

    @GetMapping("/me")
    @ApiOperation("Get currently logged user details.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentUser(@RequestHeader("Authorization") token: String): UserResourceResponse =
        userService.getCurrentUser(token)

    @PutMapping("/me/change-password")
    @ApiOperation("Change currently logged user password.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changePassword(@RequestBody user: UserChangePasswordRequest): UserResourceResponse {
        // TODO
        return UserResourceResponse("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
    }

    @PutMapping("/me/change-email")
    @ApiOperation("Change currently logged user email.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeEmail(@RequestBody user: UserChangeEmailRequest): UserResourceResponse {
        // TODO
        return UserResourceResponse("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
    }

    @PutMapping("/me/change-username")
    @ApiOperation("Change currently logged user username.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changeUsername(@RequestBody user: UserChangeUsernameRequest): UserResourceResponse {
        // TODO
        return UserResourceResponse("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
    }

    @GetMapping("/area/{areaId}/measures")
    @ApiOperation("Get measures for selected area")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getAreaMeasures(
        @RequestHeader("Authorization") token: String,
        @PathVariable("areaId") areaId: String,
        @RequestParam("from", required = false) from: Instant,
        @RequestParam("to", required = false) to: Instant
    ): List<MeasureRequest> =
        userService.getAreaMeasures(token, areaId, from, to)

    @PutMapping("/areas/{areaId}/irrigation-level")
    @ApiOperation("Set irrigation level for selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun setIrrigationLevel(
        @RequestHeader("Authorization") token: String,
        @RequestBody irrigationLevelRequest: IrrigationLevelRequest,
        @PathVariable("areaId") areaId: String
    ): AreaSettingsRequest =
        userService.setIrrigationLevel(token, areaId, irrigationLevelRequest)

    @GetMapping("/areas/settings")
    @ApiOperation("Get settings for all areas.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationSettings(@RequestHeader("Authorization") token: String): List<AreaSettingsRequest> =
        userService.getAreasSetting(token)

    @PutMapping("/me/location")
    @ApiOperation("Set location of user's device.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun setLocation(
        @RequestHeader("Authorization") token: String,
        @RequestBody locationRequest: LocationRequest
    ): UserResourceResponse =
        userService.setLocation(token, locationRequest)

    @PutMapping("/areas/{areaId}/next-irrigation")
    @ApiOperation("Set next irrigation time for selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun setNextIrrigationTime(
        @RequestHeader("Authorization") token: String,
        @RequestBody irrigationTimeRequest: NextIrrigationRequest,
        @PathVariable("areaId") areaId: String
    ): NextIrrigationRequest =
        userService.setNextIrrigationTime(token, areaId, irrigationTimeRequest)

    @PostMapping("/areas/{areaId}/irrigate-now")
    @ApiOperation("Irrigate selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun irrigateArea(
        @RequestHeader("Authorization") token: String,
        @PathVariable("areaId") areaId: String
    ): Unit =
        userService.irrigateArea(token, areaId)

    @PutMapping("/areas/{areaId}/link-sensor/{sensorId}")
    @ApiOperation("Add selected sensor to selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun linkSensorToArea(
        @RequestHeader("Authorization") token: String,
        @PathVariable("areaId") areaId: String,
        @PathVariable("sensorId") sensorId: String
    ): List<AreaRequest> =
        userService.linkSensorToArea(token, areaId, sensorId)

    @PutMapping("/areas/unlink-sensor/{sensorId}")
    @ApiOperation("Remove selected sensor from its area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun unlinkSensorFromArea(
        @RequestHeader("Authorization") token: String,
        @PathVariable("sensorId") sensorId: String
    ): List<AreaRequest> =
        userService.unlinkSensorFromArea(token, sensorId)

    @GetMapping("/sensors")
    @ApiOperation("Get all sensors.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getNotLinkedSensors(@RequestHeader("Authorization") token: String): List<SensorResponse> =
        userService.getNotLinkedSensors(token)
}