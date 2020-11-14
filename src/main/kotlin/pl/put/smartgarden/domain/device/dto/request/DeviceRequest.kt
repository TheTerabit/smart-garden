package pl.put.smartgarden.domain.device.dto.request

data class DeviceRequest(
    val secret: String,
    val sensors: List<SensorRequest>
)