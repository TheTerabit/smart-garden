package pl.put.smartgarden.domain.device.dto.request

import pl.put.smartgarden.domain.device.SensorType

data class SensorRequest(
    val type: SensorType,
    val guid: String
)