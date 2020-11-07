package pl.put.smartgarden.domain.user.dto

import java.time.Instant

data class NextIrrigationDto(
    val timestamp: Instant,
    val irrigationLevel: Int
)
