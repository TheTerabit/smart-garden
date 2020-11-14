package pl.put.smartgarden.domain.user.dto.request

import pl.put.smartgarden.domain.device.dto.request.SensorRequest

data class AreaRequest(
    val id: String,
    var sensors: List<SensorRequest>
)