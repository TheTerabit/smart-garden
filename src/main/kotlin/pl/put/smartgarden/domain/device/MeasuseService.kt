package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.repository.MeasureRepository

@Service
class MeasuseService(val measureRepository: MeasureRepository) {
    fun createMeasures(measures: List<Measure>) {
        measureRepository.saveAll(measures)
    }
}
