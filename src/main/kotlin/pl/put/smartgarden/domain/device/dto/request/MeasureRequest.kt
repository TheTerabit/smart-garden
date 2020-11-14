package pl.put.smartgarden.domain.device.dto.request

import java.time.Instant

data class MeasureRequest(
    val timestamp: Instant,
    val sensorId: String,
    val value: Double,
    val unit: String
)