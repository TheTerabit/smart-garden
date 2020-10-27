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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.DeviceFacade
import pl.put.smartgarden.domain.device.Measure
import pl.put.smartgarden.domain.device.dto.DeviceDto
import pl.put.smartgarden.domain.device.dto.DeviceMeasuresDto

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
    fun createOrUpdateDevice(@RequestBody deviceDto: DeviceDto): Device = deviceFacade.createOrUpdateDevice(deviceDto)

    @PostMapping("/measures")
    @ApiOperation("Save new measures from device.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun createMeasures(@RequestBody deviceMeasuresDto: DeviceMeasuresDto): List<Measure> = deviceFacade.createMeasures(deviceMeasuresDto)

    @GetMapping("/irrigation-decisions")
    @ApiOperation("Get decision which areas should be irrigated.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationDecisions(@RequestParam("secret") secret: String): List<Measure> = deviceFacade.getIrrigationDecisions(secret)

    @GetMapping
    fun getDevices(): List<Device> = deviceFacade.getDevices()

}