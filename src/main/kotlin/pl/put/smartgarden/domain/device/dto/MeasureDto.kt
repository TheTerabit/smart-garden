package pl.put.smartgarden.domain.device.dto

import java.time.Instant

data class MeasureDto(
    val id: String,
    val timestamp: Instant,
    val sensorId: String,
    val value: Double,
    val unit: String
)