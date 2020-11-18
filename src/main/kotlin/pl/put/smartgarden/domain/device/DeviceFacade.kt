package pl.put.smartgarden.domain.device

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.exception.NoSuchDeviceException
import pl.put.smartgarden.domain.SecurityService
import pl.put.smartgarden.domain.device.dto.response.SensorResponse

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService,
    val securityService: SecurityService
) {
    fun createOrUpdateDevice(deviceRequest: DeviceRequest): DeviceResponse {
        val device = deviceService.getDeviceBySecret(deviceRequest.secret)
        device ?: throw NoSuchDeviceException()
        val sensors = sensorService.createSensors(device.id, deviceRequest.sensors).filter { it.isActive }.map { SensorResponse(it.id, it.guid) }
        val token = securityService.generateJsonWebTokenFromId(deviceRequest.secret)
        return DeviceResponse(token, sensors)
    }

    fun createMeasures(deviceMeasures: List<MeasureRequest>, token: String): List<MeasureResponse> {
        TODO("Not yet implemented")
    }

    fun getIrrigationDecisions(token: String): List<AreaDecisionResponse> {
        TODO("Not yet implemented")
    }
}