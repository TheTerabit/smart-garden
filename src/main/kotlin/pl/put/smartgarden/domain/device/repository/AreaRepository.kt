package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Area

@Repository
interface AreaRepository : JpaRepository<Area, Int> {
    fun getAllByDeviceId(deviceId: Int) : List<Area>

    @Query("SELECT a FROM Area a WHERE a.deviceId = (SELECT d.id FROM Device d WHERE d.userId = :userId) ORDER BY a.id")
    fun getUserAreas(userId: Int) : List<Area>
}