package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
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
import pl.put.smartgarden.domain.user.dto.request.AreaSettingsRequest
import pl.put.smartgarden.domain.user.dto.request.CreateAreaRequest
import pl.put.smartgarden.domain.user.dto.request.LinkSensorRequest
import pl.put.smartgarden.domain.user.dto.request.SensorUpdateRequest
import pl.put.smartgarden.domain.user.dto.response.AreaIrrigationResponse
import pl.put.smartgarden.domain.user.dto.response.AreaResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
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

    @GetMapping("/areas/measures")
    @ApiOperation("Get all areas measures, humidity, temperature and illumination represents last measure.")
    @ResponseStatus(HttpStatus.OK)
    fun getAllAreasMeasures(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestParam("from", required = false) from: Instant?,
        @RequestParam("to", required = false) to: Instant?): List<AreaResponse> {
        return userDeviceService.getAllAreasMeasures(userId, from, to)
    }

    @GetMapping("/areas/{areaId}/measures")
    @ApiOperation("Get measures for selected area, humidity, temperature and illumination represents last measure.")
    @ResponseStatus(HttpStatus.OK)
    fun getAreaMeasures(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int,
        @RequestParam("from", required = false) from: Instant?,
        @RequestParam("to", required = false) to: Instant?
    ): AreaResponse =
        userDeviceService.getAreaMeasures(userId, areaId, from, to)

    @GetMapping("/areas/settings")
    @ApiOperation("Get settings for all areas.")
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationSettings(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int): List<AreaSettingsResponse> =
        userDeviceService.getAreasSetting(userId)

    @GetMapping("/areas/{areaId}/settings")
    @ApiOperation("Get settings for given area.")
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationSettings(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int): AreaSettingsResponse =
        userDeviceService.getAreaSettings(userId, areaId)

    @PutMapping("/areas/{areaId}/settings")
    @ApiOperation("Set settings for area.")
    @ResponseStatus(HttpStatus.OK)
    fun setIrrigationSettings(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int,
        @RequestBody areaSettingsRequest: AreaSettingsRequest): AreaSettingsResponse =
        userDeviceService.setAreaSettings(userId, areaId, areaSettingsRequest)

    @GetMapping("/areas/{areaId}/irrigations")
    @ApiOperation("Get historical irrigations of selected area.")
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigations(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int,
        @RequestParam("from", required = false) from: Instant?,
        @RequestParam("to", required = false) to: Instant?
    ):  List<AreaIrrigationResponse>  =
        userDeviceService.getIrrigations(userId, areaId, from, to)

    @PostMapping("/areas/{areaId}/irrigations")
    @ApiOperation("Irrigate selected area.")
    @ResponseStatus(HttpStatus.OK)
    fun irrigateArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int) = userDeviceService.irrigateArea(userId, areaId)

    @PutMapping("/areas/{areaId}/sensors}")
    @ApiOperation("Add sensor to selected area.")
    @ResponseStatus(HttpStatus.OK)
    fun linkSensorToArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int,
        @RequestBody linkSensorRequest: LinkSensorRequest
    ): SimpleAreaResponse =
        userDeviceService.linkSensorToArea(userId, areaId, linkSensorRequest)

    @DeleteMapping("/areas/{areaId}/sensors/{sensorGuid}")
    @ApiOperation("Remove selected sensor from its area.")
    @ResponseStatus(HttpStatus.OK)
    fun unlinkSensorFromArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("sensorGuid") sensorGuid: String,
        @PathVariable("areaId") areaId: Int
    ): SimpleAreaResponse =
        userDeviceService.unlinkSensorFromArea(userId, areaId, sensorGuid)

    @GetMapping("/areas")
    @ApiOperation("Get all areas simple info.")
    @ResponseStatus(HttpStatus.OK)
    fun getAreasInfo(@ApiParam(hidden = true) @RequestAttribute("id") userId: Int): List<SimpleAreaResponse> {
        return userDeviceService.getAreasInfo(userId)
    }

    @GetMapping("/areas/{areaId}")
    @ApiOperation("Get area simple info.")
    @ResponseStatus(HttpStatus.OK)
    fun getAreaInfo(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int): SimpleAreaResponse {
        return userDeviceService.getAreaInfo(userId, areaId)
    }

    @PostMapping("/areas")
    @ApiOperation("Create new area.")
    @ResponseStatus(HttpStatus.OK)
    fun createArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestBody createAreaRequest: CreateAreaRequest): SimpleAreaResponse {
        return userDeviceService.createArea(userId, createAreaRequest)
    }

    @DeleteMapping("/areas/{areaId}")
    @ApiOperation("Delete area.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteArea(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("areaId") areaId: Int) {
        return userDeviceService.deleteArea(userId, areaId)
    }

    @GetMapping("/sensors")
    @ApiOperation("Get all sensors connected to user device.")
    @ResponseStatus(HttpStatus.OK)
    fun getAllSensors(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @RequestParam("active", required = false) active: Boolean?
    ): List<SensorResponse> = userDeviceService.getAllSensors(userId, active)

    @GetMapping("/sensors/{sensorGuid}")
    @ApiOperation("Get sensor connected to user device.")
    @ResponseStatus(HttpStatus.OK)
    fun getSensor(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("sensorGuid") sensorGuid: String
    ): SensorResponse = userDeviceService.getSensor(userId, sensorGuid)

    @PutMapping("/sensors/{sensorGuid}")
    @ApiOperation("Update sensor (activate / deactivate) connected to user device.")
    @ResponseStatus(HttpStatus.OK)
    fun updateSensor(
        @ApiParam(hidden = true) @RequestAttribute("id") userId: Int,
        @PathVariable("sensorGuid") sensorGuid: String,
        @RequestBody request: SensorUpdateRequest
    ): SensorResponse = userDeviceService.updateSensor(userId, sensorGuid, request)
}