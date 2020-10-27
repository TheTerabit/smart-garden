package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.DeviceDto
import pl.put.smartgarden.domain.device.dto.DeviceMeasuresDto
import pl.put.smartgarden.domain.device.response.DeviceResponse
import pl.put.smartgarden.domain.device.response.MeasureResponse

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService
) {

    fun createOrUpdateDevice(deviceDto: DeviceDto): DeviceResponse {
        deviceService.createDevice()
        sensorService.createSensors()
        TODO()
    }

    fun getDevices(): List<Device> = deviceService.getDevices()

    fun createMeasures(deviceMeasuresDto: DeviceMeasuresDto): List<MeasureResponse> {
        TODO("Not yet implemented")
    }

    fun getIrrigationDecisions(secret: String): List<MeasureResponse> {
        TODO("Not yet implemented")
    }
}