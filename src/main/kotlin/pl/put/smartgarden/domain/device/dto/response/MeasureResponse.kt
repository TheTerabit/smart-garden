package pl.put.smartgarden.domain.device.dto.response

import java.time.Instant

data class MeasureResponse(
    val id: Int,
    val timestamp: Instant,
    val sensorId: Int,
    val value: Int,
    val unit: String
)