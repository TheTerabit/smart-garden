package pl.put.smartgerden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgerden.domain.device.dto.DeviceDto

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService
) {

    fun createDevice(deviceDto: DeviceDto) {
        deviceService.createDevice()
        sensorService.createSensors()
    }

    fun getDevices(): List<Device> = deviceService.getDevices()
}