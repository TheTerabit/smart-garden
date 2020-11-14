package pl.put.smartgarden.domain.device.dto.response

import java.time.Instant

data class MeasureResponse(
    val id: String,
    val timestamp: Instant,
    val sensorId: String,
    val value: Double,
    val unit: String
)