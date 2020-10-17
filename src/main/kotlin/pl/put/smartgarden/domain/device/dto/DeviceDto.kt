package pl.put.smartgarden.domain.device.dto

data class DeviceDto(
    val id: String,
    val sensors: List<SensorDto>
)

data class SensorDto(
    val type: String,
    val number: Int
)