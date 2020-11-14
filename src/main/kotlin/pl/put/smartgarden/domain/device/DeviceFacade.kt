package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.request.SensorRequest
import pl.put.smartgarden.domain.device.dto.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.response.SensorResponse

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService
) {
    fun createOrUpdateDevice(deviceRequest: DeviceRequest): DeviceResponse {
        deviceService.createDevice()
        sensorService.createSensors()
        TODO()
    }

    fun getDevices(): List<Device> = deviceService.getDevices()

    fun createMeasures(deviceMeasuresRequest: List<MeasureRequest>, token: String): List<MeasureResponse> {
        TODO("Not yet implemented")
    }

    fun getIrrigationDecisions(token: String): List<AreaDecisionResponse> {
        TODO("Not yet implemented")
    }
}