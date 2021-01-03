package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Measure
import pl.put.smartgarden.domain.device.SensorType
import java.time.Instant

@Repository
interface MeasureRepository : JpaRepository<Measure, Int>
{
    @Query("SELECT m FROM Measure m WHERE m.sensorId IN (SELECT s.id FROM Sensor s WHERE s.deviceId = :deviceId AND s.areaId = :areaId AND s.isActive = true AND s.type = :type) AND m.timestamp > :from AND m.timestamp <= :to ORDER BY m.timestamp")
    fun findMeasures(@Param("deviceId") deviceId: Int, @Param("areaId") areaId: Int, @Param("type") type: SensorType, @Param("from") from: Instant, @Param("to") to: Instant): List<Measure>

    @Modifying
    @Query("DELETE FROM Measure m WHERE m.areaId = :areaId")
    fun removeAllByAreaId(@Param("areaId") areaId: Int)

    @Modifying
    @Query("DELETE FROM Measure m WHERE m.areaId IS NULL")
    fun removeAllByNullAreaId()
}