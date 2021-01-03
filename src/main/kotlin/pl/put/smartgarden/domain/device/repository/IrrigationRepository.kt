package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Irrigation
import pl.put.smartgarden.domain.device.Measure
import pl.put.smartgarden.domain.device.SensorType
import java.time.Instant

@Repository
interface IrrigationRepository : JpaRepository<Irrigation, Int> {
    @Query(nativeQuery = true,
        value = "select  a.timestamp as timestamp, a.area_id as area_id, a.amount as amount, a.id as id from irrigations a inner join (select area_id, max(timestamp) timestamp from irrigations GROUP BY area_id HAVING area_id = :areaId) b on a.timestamp = b.timestamp and a.area_id = b.area_id\n")
    fun getLastIrrigation(@Param("areaId") areaId: Int): List<Irrigation>
}
