package pl.put.smartgarden.domain.user.dto.response

import java.time.Instant

data class AreaResponse(
    val id: Int,
    val nextWateringTime: Instant,
    val temperature: Int,
    val humidity: Int,
    val illuminance: Int,
    var temperatureMeasures: List<AreaSensorMeasuresResponse>,
    var illuminanceMeasures: List<AreaSensorMeasuresResponse>,
    var humidityMeasures: List<AreaSensorMeasuresResponse>
)

data class AreaSensorMeasuresResponse(
    val timestamp : Instant,
    val value: Int
)

data class SimpleAreaResponse(
    val id: Int,
    val sensors: List<SimpleAreaSensorResponse>
)

data class SimpleAreaSensorResponse(
    val guid: String,
    val type: String,
    val unit: String,
    val active: Boolean
)

data class MeasureResponse(
    val sensorGuid: String,
    val type: String,
    val unit: String,
    val measures: List<MeasureMeasuresResponse>
)

class MeasureMeasuresResponse (
    val timestamp: Instant,
    val value: Int
)

data class SensorResponse(
    val guid: String,
    val type: String,
    val unit: String,
    val areaId: Int?,
    val active: Boolean
)
