package pl.put.smartgarden.domain.device.dto.response

data class AreaDecisionResponse(
    val sensorGuid: String,
    val decision: Boolean
)
