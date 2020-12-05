package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.user.UserDeviceService
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.dto.response.AreaResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
import java.time.Instant

@Api(description = "User device frontend API")
@CrossOrigin
@RestController
@RequestMapping("/users/me/device")
class UserDeviceController(
    val userDeviceService: UserDeviceService
) {
    @GetMapping("/area/{areaId}/measures")
    @ApiOperation("Get measures for selected area")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getAreaMeasures(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: String,
        @RequestParam("from", required = false) from: Instant,
        @RequestParam("to", required = false) to: Instant
    ): List<MeasureResponse> =
        userDeviceService.getAreaMeasures(userId, areaId, from, to)

    @PutMapping("/areas/{areaId}/irrigation-level")
    @ApiOperation("Set irrigation level for selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun setIrrigationLevel(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestBody irrigationLevelRequest: IrrigationLevelRequest,
        @PathVariable("areaId") areaId: String
    ): AreaSettingsResponse =
        userDeviceService.setIrrigationLevel(userId, areaId, irrigationLevelRequest)

    @GetMapping("/areas/settings")
    @ApiOperation("Get settings for all areas.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationSettings(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int): List<AreaSettingsResponse> =
        userDeviceService.getAreasSetting(userId)

    @PutMapping("/areas/{areaId}/next-irrigation")
    @ApiOperation("Set next irrigation time for selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun setNextIrrigationTime(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestBody irrigationTimeRequest: NextIrrigationRequest,
        @PathVariable("areaId") areaId: String
    ): NextIrrigationRequest =
        userDeviceService.setNextIrrigationTime(userId, areaId, irrigationTimeRequest)

    @PostMapping("/areas/{areaId}/irrigate-now")
    @ApiOperation("Irrigate selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun irrigateArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: String
    ): Unit =
        userDeviceService.irrigateArea(userId, areaId)

    @PutMapping("/areas/{areaId}/link-sensor/{sensorId}")
    @ApiOperation("Add selected sensor to selected area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun linkSensorToArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: String,
        @PathVariable("sensorId") sensorId: String
    ): List<AreaResponse> =
        userDeviceService.linkSensorToArea(userId, areaId, sensorId)

    @PutMapping("/areas/unlink-sensor/{sensorId}")
    @ApiOperation("Remove selected sensor from its area.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun unlinkSensorFromArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("sensorId") sensorId: String
    ): List<AreaResponse> =
        userDeviceService.unlinkSensorFromArea(userId, sensorId)

    @GetMapping("/sensors")
    @ApiOperation("Get all sensors.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getNotLinkedSensors(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int): List<SensorResponse> =
        userDeviceService.getNotLinkedSensors(userId)

    @GetMapping("/areas")
    @ApiOperation("Get all areas.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 403, message = "Unactivated")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getAvailableAreas(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int) : List<AreaResponse>
    {
        return userDeviceService.getAvailableAreas(userId)
    }

}