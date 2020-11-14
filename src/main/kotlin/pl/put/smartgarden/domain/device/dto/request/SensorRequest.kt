package pl.put.smartgarden.domain.device.dto.request

data class SensorRequest(
    val type: String,
    val guid: String
)