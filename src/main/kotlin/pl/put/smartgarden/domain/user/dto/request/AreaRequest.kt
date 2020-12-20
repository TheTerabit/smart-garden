package pl.put.smartgarden.domain.user.dto.request

import pl.put.smartgarden.domain.device.TimeUnit

data class AreaSettingsRequest(
    val areaId: Int,
    val frequencyUnit: TimeUnit?,
    val frequencyValue: Int?,
    val isIrrigationEnabled: Boolean?,
    val isWeatherEnabled: Boolean?,
    val strength: Int?,
    val threshold: Int?
)
