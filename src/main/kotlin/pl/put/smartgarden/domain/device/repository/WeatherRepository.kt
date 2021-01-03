package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Weather
import java.util.*

@Repository
interface WeatherRepository : JpaRepository<Weather, Int> {
    fun findByDeviceId(deviceId: Int): Optional<Weather>
}