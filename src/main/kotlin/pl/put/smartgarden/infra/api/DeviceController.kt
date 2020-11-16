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
import pl.put.smartgarden.domain.device.DeviceService
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse

@Api(description = "Devices api")
@RestController
@RequestMapping("/devices")
class DeviceController(
    val deviceService: DeviceService
) {
    @PutMapping
    @ApiOperation("Create or update device.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request"),
        ApiResponse(code = 409, message = "Conflict")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun createOrUpdateDevice(@RequestBody deviceRequest: DeviceRequest): DeviceResponse =
        TODO()

    @PostMapping("/measures")
    @ApiOperation("Save new measures from device.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun createMeasures(
        @RequestBody deviceMeasures: List<MeasureRequest>,
        @RequestHeader("Authorization") token: String
    ): List<MeasureResponse> =
        TODO()

    @GetMapping("/irrigation-decisions")
    @ApiOperation("Get decision which areas should be irrigated.")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK"),
        ApiResponse(code = 400, message = "Bad request")
    ])
    @ResponseStatus(HttpStatus.OK)
    fun getIrrigationDecisions(@RequestHeader("Authorization") token: String): List<AreaDecisionResponse> =
        TODO()
}