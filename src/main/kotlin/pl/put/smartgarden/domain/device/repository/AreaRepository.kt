package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Area

@Repository
interface AreaRepository : JpaRepository<Area, Int> {
    fun getAllByDeviceId(deviceId: Int) : List<Area>
}