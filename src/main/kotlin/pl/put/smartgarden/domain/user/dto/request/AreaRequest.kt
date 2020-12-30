package pl.put.smartgarden.domain.user.dto.request

import pl.put.smartgarden.domain.device.TimeUnit

data class AreaSettingsRequest(
    val frequencyUnit: TimeUnit?,
    val frequencyValue: Int?,
    val isIrrigationEnabled: Boolean?,
    val isWeatherEnabled: Boolean?,
    val strength: Int?,
    val threshold: Int?
)

data class CreateAreaRequest(
    val settings: AreaSettingsRequest,
    val sensors: List<String>
)

data class LinkSensorRequest(
    val sensorGuid: String,
    val active: Boolean?
)

data class SensorUpdateRequest(
    val active: Boolean
)