package pl.put.smartgarden.domain.device.dto.request

data class DeviceRequest(
    val guid: String,
    val sensors: List<SensorRequest>
)