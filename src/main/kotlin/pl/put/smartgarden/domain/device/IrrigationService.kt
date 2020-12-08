package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.IrrigationRepository
import java.time.Instant

@Service
class IrrigationService(val irrigationRepository: IrrigationRepository) {
    fun createIrrigation(area: Area, irrigationValue: Int): Irrigation {
        val irrigation = Irrigation(Instant.now(), area.id, irrigationValue)
        irrigationRepository.save(irrigation)
        return irrigation
    }
}
