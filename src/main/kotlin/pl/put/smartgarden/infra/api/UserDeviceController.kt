package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.user.UserDeviceService
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.dto.response.AreaResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
import pl.put.smartgarden.domain.user.dto.response.MeasureResponse
import pl.put.smartgarden.domain.user.dto.response.SensorResponse
import pl.put.smartgarden.domain.user.dto.response.SimpleAreaResponse
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
    @ResponseStatus(HttpStatus.OK)
    fun getAreaMeasures(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int,
        @RequestParam("from", required = false) from: Instant?,
        @RequestParam("to", required = false) to: Instant?
    ): List<MeasureResponse> =
        userDeviceService.getAreaMeasures(userId, areaId, from, to)

    @PutMapping("/areas/{areaId}/irrigation-level")
    @ApiOperation("Set irrigation level for selected area.")
    @ResponseStatus(HttpStatus.OK)
    fun setIrrigationLevel(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestBody irrigationLevelRequest: IrrigationLevelRequest,
        @PathVariable("areaId") areaId: String
    ): AreaSettingsResponse =
        userDeviceService.setIrrigationLevel(userId, areaId, irrigationLevelRequest)

    @GetMapping("/areas/settings")
    @ApiOperation("Get settings for all areas.")
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationSettings(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int): List<AreaSettingsResponse> =
        userDeviceService.getAreasSetting(userId)

    @GetMapping("/areas/{areaId}/settings")
    @ApiOperation("Get settings for all areas.")
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationSettings(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int): AreaSettingsResponse =
        userDeviceService.getAreaSettings(userId, areaId)

    @PutMapping("/areas/{areaId}/next-irrigation")
    @ApiOperation("Set next irrigation time for selected area.")
    @ResponseStatus(HttpStatus.OK)
    fun setNextIrrigationTime(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestBody irrigationTimeRequest: NextIrrigationRequest,
        @PathVariable("areaId") areaId: String
    ): NextIrrigationRequest =
        userDeviceService.setNextIrrigationTime(userId, areaId, irrigationTimeRequest)

    @PostMapping("/areas/{areaId}/irrigate-now")
    @ApiOperation("Irrigate selected area.")
    @ResponseStatus(HttpStatus.OK)
    fun irrigateArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: String
    ): Unit =
        userDeviceService.irrigateArea(userId, areaId)

    @PutMapping("/areas/{areaId}/link-sensor/{sensorGuid}")
    @ApiOperation("Add selected sensor to selected area.")
    @ResponseStatus(HttpStatus.OK)
    fun linkSensorToArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int,
        @PathVariable("sensorGuid") sensorGuid: String
    ): List<SimpleAreaResponse> =
        userDeviceService.linkSensorToArea(userId, areaId, sensorGuid)

    @PutMapping("/areas/unlink-sensor/{sensorGuid}")
    @ApiOperation("Remove selected sensor from its area.")
    @ResponseStatus(HttpStatus.OK)
    fun unlinkSensorFromArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("sensorGuid") sensorGuid: String
    ): List<SimpleAreaResponse> =
        userDeviceService.unlinkSensorFromArea(userId, sensorGuid)

    @GetMapping("/sensors")
    @ApiOperation("Get all sensors.")
    @ResponseStatus(HttpStatus.OK)
    fun getAllSensors(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestParam("active", required = false) active: Boolean?
    ): List<SensorResponse> =
        userDeviceService.getAllSensors(userId, active)

    @GetMapping("/areas")
    @ApiOperation("Get all areas measures.")
    @ResponseStatus(HttpStatus.OK)
    fun getAllAreasMeasures(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestParam("from", required = false) from: Instant?,
        @RequestParam("to", required = false) to: Instant?): List<AreaResponse> {
        return userDeviceService.getAllAreasMeasures(userId, from, to)
    }

    @GetMapping("/areas-info")
    @ApiOperation("Get all areas simple info.")
    @ResponseStatus(HttpStatus.OK)
    fun getAreasInfo(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int): List<SimpleAreaResponse> {
        return userDeviceService.getAreasInfo(userId)
    }

}