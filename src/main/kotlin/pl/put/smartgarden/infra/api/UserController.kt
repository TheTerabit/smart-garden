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
import pl.put.smartgarden.domain.device.response.SensorResponse
import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.AreaDto
import pl.put.smartgarden.domain.user.dto.AreaSettingsDto
import pl.put.smartgarden.domain.user.dto.IrrigationLevelDto
import pl.put.smartgarden.domain.user.dto.IrrigationTimeDto
import pl.put.smartgarden.domain.user.dto.LocationDto
import pl.put.smartgarden.domain.user.dto.MeasuresDto
import pl.put.smartgarden.domain.user.dto.NextIrrigationDto
import pl.put.smartgarden.domain.user.dto.UserChangeEmailDto
import pl.put.smartgarden.domain.user.dto.UserChangePasswordDto
import pl.put.smartgarden.domain.user.dto.UserChangeUsernameDto
import pl.put.smartgarden.domain.user.dto.UserResourceDto
import pl.put.smartgarden.domain.user.dto.UserSignInDto
import pl.put.smartgarden.domain.user.dto.UserSignInResponseDto
import pl.put.smartgarden.domain.user.dto.UserSignUpDto
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
    fun signUp(@Valid @RequestBody user: UserSignUpDto) = userService.signUpUser(user)

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
    fun signIn(@RequestBody user: UserSignInDto): UserSignInResponseDto = userService.signIn(user)

    @GetMapping
    @ApiOperation("Get all users.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getUsers(): List<UserResourceDto> = userService.getUsers()

    @GetMapping("/me")
    @ApiOperation("Get currently logged user details.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getCurrentUser(@RequestHeader("Authorization") token: String): UserResourceDto =
        userService.getCurrentUser(token)

    @PutMapping("/me/change-password")
    @ApiOperation("Change currently logged user password.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun changePassword(@RequestBody user: UserChangePasswordDto): UserResourceDto {
        // TODO
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
    fun changeEmail(@RequestBody user: UserChangeEmailDto): UserResourceDto {
        // TODO
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
    fun changeUsername(@RequestBody user: UserChangeUsernameDto): UserResourceDto {
        // TODO
        return UserResourceDto("nazwauzytkownika", "uzytkownik@gmail.com", "123dasads123")
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
    ): MeasuresDto =
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
        @RequestBody irrigationLevelDto: IrrigationLevelDto,
        @PathVariable("areaId") areaId: String
    ): AreaSettingsDto =
        userService.setIrrigationLevel(token, areaId, irrigationLevelDto)

    @GetMapping("/areas/settings")
    @ApiOperation("Get settings for all areas.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationSettings(@RequestHeader("Authorization") token: String): List<AreaSettingsDto> =
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
        @RequestBody locationDto: LocationDto
    ): UserResourceDto =
        userService.setLocation(token, locationDto)

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
        @RequestBody irrigationTimeDto: IrrigationTimeDto,
        @PathVariable("areaId") areaId: String
    ): NextIrrigationDto =
        userService.setNextIrrigationTime(token, areaId, irrigationTimeDto)

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
    ): List<AreaDto> =
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
    ): List<AreaDto> =
        userService.unlinkSensorFromArea(token, sensorId)

    @GetMapping("/sensors")
    @ApiOperation("Get not linked sensors.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getNotLinkedSensors(@RequestHeader("Authorization") token: String): List<SensorResponse> =
        userService.getNotLinkedSensors(token)
}