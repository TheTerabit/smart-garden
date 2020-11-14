package pl.put.smartgarden.domain.device.dto.response

data class DeviceResponse(
    val accessToken: String,
    val sensors: List<SensorResponse>
)