package pl.put.smartgarden.domain.user.dto.response

import pl.put.smartgarden.domain.device.dto.request.SensorRequest

data class AreaResponse(
    val id: String,
    var sensors: List<SensorRequest>
)