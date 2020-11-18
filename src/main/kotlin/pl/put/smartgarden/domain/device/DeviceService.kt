package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.DeviceRepository

@Service
class DeviceService(val deviceRepository: DeviceRepository) {
    fun getDeviceBySecret(guid: String): Device? =
        deviceRepository.findDeviceByGuid(guid)
}
