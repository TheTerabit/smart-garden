package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.MeasureDto
import pl.put.smartgarden.domain.device.dto.SensorDto
import pl.put.smartgarden.domain.device.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.response.MeasureResponse
import pl.put.smartgarden.domain.device.response.SensorResponse

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService
) {
    fun createOrUpdateDevice(sensors: List<SensorDto>): List<SensorResponse> {
        deviceService.createDevice()
        sensorService.createSensors()
        TODO()
    }

    fun getDevices(): List<Device> = deviceService.getDevices()

    fun createMeasures(deviceMeasuresDto: List<MeasureDto>, token: String): List<MeasureResponse> {
        TODO("Not yet implemented")
    }

    fun getIrrigationDecisions(token: String): List<AreaDecisionResponse> {
        TODO("Not yet implemented")
    }
}