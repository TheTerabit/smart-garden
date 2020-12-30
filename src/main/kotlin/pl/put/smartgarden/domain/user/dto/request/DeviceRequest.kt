package pl.put.smartgarden.domain.user.dto.request

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull


data class LocationRequest(
    @field:NotNull(message = "{validation.user.latitude.not-empty}")
    @field:Min(value = -180, message = "{validation.user.latitude.not-empty}")
    @field:Max(value = 180, message = "{validation.user.latitude.not-empty}")
    val latitude: Double,
    @field:NotNull(message = "{validation.user.longitude.not-empty}")
    @field:Min(value = -90, message = "{validation.user.longitude.not-empty}")
    @field:Max(value = 90, message = "{validation.user.longitude.not-empty}")
    val longitude: Double
)
