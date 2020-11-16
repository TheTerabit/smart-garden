package pl.put.smartgarden.domain.user.dto.request

import javax.validation.constraints.NotBlank

class LocationRequest(
    @field:NotBlank(message = "{validation.user.location.not-empty}")
    val longitude: Double,
    @field:NotBlank(message = "{validation.user.location.not-empty}")
    val latitude: Double
)
