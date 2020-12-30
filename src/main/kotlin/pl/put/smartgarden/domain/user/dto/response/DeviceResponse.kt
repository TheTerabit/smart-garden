package pl.put.smartgarden.domain.user.dto.response

import java.time.Instant

data class NextIrrigationResponse(
    val timestamp: Instant,
    val amount: Int
)