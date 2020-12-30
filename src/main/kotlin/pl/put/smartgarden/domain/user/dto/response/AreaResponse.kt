package pl.put.smartgarden.domain.user.dto.response

import pl.put.smartgarden.domain.device.TimeUnit
import java.time.Instant

data class AreaResponse(
    val id: Int,
    val nextWateringTime: Instant?,
    val temperature: Int,
    val humidity: Int,
    val illuminance: Int,
    var temperatureMeasures: List<AreaSensorMeasuresResponse>,
    var illuminanceMeasures: List<AreaSensorMeasuresResponse>,
    var humidityMeasures: List<AreaSensorMeasuresResponse>
)

data class AreaSettingsResponse(
    val areaId: Int,
    val frequencyUnit: TimeUnit,
    val frequencyValue: Int,
    val isIrrigationEnabled: Boolean,
    val isWeatherEnabled: Boolean,
    val strength: Int,
    val threshold: Int
)

data class AreaSensorMeasuresResponse(
    val timestamp: Instant,
    val value: Int
)

data class SimpleAreaResponse(
    val id: Int,
    val settings: AreaSettingsResponse,
    val sensors: List<SimpleAreaSensorResponse>
)

data class SimpleAreaSensorResponse(
    val guid: String,
    val type: String,
    val unit: String,
    val active: Boolean
)

data class SensorResponse(
    val guid: String,
    val type: String,
    val unit: String,
    val areaId: Int?,
    val active: Boolean
)

data class AreaIrrigationResponse(
    val timestamp: Instant,
    val amount: Int
)