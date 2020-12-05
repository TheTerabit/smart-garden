package pl.put.smartgarden.domain.user.dto.response

import java.time.Instant

data class AreaResponse(
    val id: Int,
    val nextWateringTime: Instant,
    val temperature: Int,
    val humidity: Int,
    val illuminance: Int,
    var temperatureMeasures: List<AreaSensorResponse>,
    var illuminanceMeasures: List<AreaSensorResponse>,
    var humidityMeasures: List<AreaSensorResponse>
)

data class AreaSensorResponse(
    val timestamp : Instant,
    val value: Int
)