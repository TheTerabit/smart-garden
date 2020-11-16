package pl.put.smartgarden.domain.user.dto.response

import java.time.Instant

data class AreaResponse(
    val id: String,
    val nextWateringTime: Instant,
    val temperature: Int,
    val humidity: Int,
    val illuminance: Int,
    var temperatureMeasures: List<SensorResponse>,
    var illuminanceMeasures: List<SensorResponse>,
    var humidityMeasures: List<SensorResponse>
)

data class SensorResponse(
    val timestamp : Instant,
    val value: Int
)