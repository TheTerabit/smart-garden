package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService
) {
    fun createOrUpdateDevice(deviceRequest: DeviceRequest): DeviceResponse {
        TODO()
    }

    fun getDevices(): List<Device> = deviceService.getDevices()

    fun createMeasures(deviceMeasuresRequest: List<MeasureRequest>, token: String): List<MeasureResponse> {
        TODO("Not yet implemented")
    }

    fun getIrrigationDecisions(token: String): List<AreaDecisionResponse> {
        TODO("Not yet implemented")
    }

    fun addUserDevice(deviceGuid: String, latitude: Double, longitude: Double, userId: Int) {
        deviceService.createDevice(deviceGuid, latitude, longitude, userId)
    }
}