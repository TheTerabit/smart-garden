package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.DeviceDto
import pl.put.smartgarden.domain.device.dto.DeviceMeasuresDto

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService
) {

    fun createOrUpdateDevice(deviceDto: DeviceDto): Device {
        deviceService.createDevice()
        sensorService.createSensors()
        TODO()
    }

    fun getDevices(): List<Device> = deviceService.getDevices()

    fun createMeasures(deviceMeasuresDto: DeviceMeasuresDto): List<Measure> {
        TODO("Not yet implemented")
    }

    fun getIrrigationDecisions(secret: String): List<Measure> {
        TODO("Not yet implemented")
    }
}