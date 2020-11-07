package pl.put.smartgarden.infra.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.DeviceFacade
import pl.put.smartgarden.domain.device.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.MeasureDto
import pl.put.smartgarden.domain.device.dto.SensorDto
import pl.put.smartgarden.domain.device.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.response.SensorResponse

@Api(description = "Devices api")
@RestController
@RequestMapping("/devices")
class DeviceController(
    val deviceFacade: DeviceFacade
) {
    @PutMapping
    @ApiOperation("Create or update device.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 409, message = "Conflict")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun createOrUpdateDevice(@RequestBody sensors: List<SensorDto>): List<SensorResponse> =
        deviceFacade.createOrUpdateDevice(sensors)

    @PostMapping("/{id}/measures")
    @ApiOperation("Save new measures from device.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun createMeasures(
        @RequestBody deviceMeasures: List<MeasureDto>,
        @RequestHeader("Authorization") token: String
    ): List<MeasureResponse> =
        deviceFacade.createMeasures(deviceMeasures, token)

    @GetMapping("/irrigation-decisions")
    @ApiOperation("Get decision which areas should be irrigated.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationDecisions(@RequestHeader("Authorization") token: String): List<AreaDecisionResponse> =
        deviceFacade.getIrrigationDecisions(token)

    @GetMapping
    fun getDevices(): List<Device> = deviceFacade.getDevices()
}