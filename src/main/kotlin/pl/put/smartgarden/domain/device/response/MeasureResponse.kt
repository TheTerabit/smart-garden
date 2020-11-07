package pl.put.smartgarden.domain.device.response

import java.time.Instant

data class MeasureResponse(
    val timestamp: Instant,
    val sensorId: String,
    val value: Double,
    val unit: String
)