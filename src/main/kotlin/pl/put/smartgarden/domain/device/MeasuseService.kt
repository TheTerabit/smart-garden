package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.repository.MeasureRepository

@Service
class MeasuseService(val measureRepository: MeasureRepository) {
    fun createMeasures(deviceMeasures: List<MeasureRequest>): List<Measure>  {
        val measures = deviceMeasures.map { measure -> Measure(measure.timestamp, measure.value, measure.sensorId) }
        measureRepository.saveAll(measures)
        return measures
    }

}
