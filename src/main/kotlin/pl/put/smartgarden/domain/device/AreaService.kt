package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.AreaRepository

@Service
class AreaService(val areaRepository: AreaRepository) {
    fun getAreaById(areaId: Int): Area? = areaRepository.findById(areaId).orElse(null)
    fun getAllAreas() = areaRepository.findAll()
}
