package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Device

@Repository
interface DeviceRepository : JpaRepository<Device, Int> {
    fun findDeviceByGuid(guid: String): Device?
    fun existsByGuid(guid: String): Boolean
}