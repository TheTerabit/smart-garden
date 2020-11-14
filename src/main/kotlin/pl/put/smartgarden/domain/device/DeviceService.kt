package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.DeviceRepository

@Service
class DeviceService(
    val deviceRepository: DeviceRepository
) {

    fun getDevices() = deviceRepository.findAll()

    fun createDevice(deviceGuid: String, latitude: Double, longitude: Double, userId: Int) {
        val device = Device(
            guid = deviceGuid,
            latitude = latitude,
            longitude = longitude,
            userId = userId,
            sensors = mutableListOf(),
            areas = mutableListOf()
        )

        deviceRepository.save(device)
    }
}