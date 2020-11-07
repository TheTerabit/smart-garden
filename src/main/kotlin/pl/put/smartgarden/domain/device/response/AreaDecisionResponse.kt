package pl.put.smartgarden.domain.device.response

data class AreaDecisionResponse(
    val sensorGuid: String,
    val decision: Boolean
)
