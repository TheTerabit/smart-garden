package pl.put.smartgarden.domain.device.dto.request

import java.time.Instant

data class MeasureRequest(
    val timestamp: Instant,
    val sensorId: Int,
    val value: Double
)