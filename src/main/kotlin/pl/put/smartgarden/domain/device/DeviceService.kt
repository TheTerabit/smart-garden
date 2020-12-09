package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import java.util.*

@Service
class DeviceService(val deviceRepository: DeviceRepository) {
    fun getDeviceByGuid(guid: String): Device? =
        deviceRepository.findDeviceByGuid(guid)
    fun getDeviceById(id: Int): Optional<Device> =
        deviceRepository.findById(id)
}
