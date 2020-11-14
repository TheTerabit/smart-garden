package pl.put.smartgarden.domain.user.dto.request

import java.time.Instant

data class NextIrrigationRequest(
    val timestamp: Instant,
    val irrigationLevel: Int
)
