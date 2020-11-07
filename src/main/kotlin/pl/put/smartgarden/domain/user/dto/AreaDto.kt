package pl.put.smartgarden.domain.user.dto

import pl.put.smartgarden.domain.device.dto.SensorDto

data class AreaDto(
    val id: String,
    var sensors: List<SensorDto>
)