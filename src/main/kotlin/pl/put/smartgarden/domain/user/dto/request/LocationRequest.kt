package pl.put.smartgarden.domain.user.dto.request

import javax.validation.constraints.NotNull

class LocationRequest(
    @field:NotNull(message = "{validation.user.location.not-empty}")
    val latitude: Double,
    @field:NotNull(message = "{validation.user.location.not-empty}")
    val longitude: Double
)
