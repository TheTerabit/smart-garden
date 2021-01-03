package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.AreaRepository
import pl.put.smartgarden.domain.device.repository.AreaSettingsRepository

@Service
class AreaService(
    val areaRepository: AreaRepository,
    val areaSettingsRepository: AreaSettingsRepository
) {
    fun getAreaById(areaId: Int): Area? = areaRepository.findById(areaId).orElse(null)
    fun getAllAreas() = areaRepository.findAll()
    fun noLongerIrrigateNow(area: Area) {
        val areaSettings = area.settings
        areaSettings.irrigateNow = false
        areaSettingsRepository.saveAndFlush(areaSettings)
    }
}
