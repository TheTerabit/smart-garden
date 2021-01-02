package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Sensor
import java.util.Optional

@Repository
interface SensorRepository : JpaRepository<Sensor, Int> {
    fun findAllByDeviceId(deviceId: Int): List<Sensor>
    fun findAllByGuidIn(guids: List<String>): List<Sensor>
    fun findByGuid(guid: String): Optional<Sensor>
    fun findAllByAreaId(areaId: String?): List<Sensor>
}