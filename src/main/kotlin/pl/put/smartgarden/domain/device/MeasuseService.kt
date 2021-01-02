package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.repository.MeasureRepository

@Service
class MeasuseService(val measureRepository: MeasureRepository) {
    fun createMeasure(deviceMeasure: MeasureRequest, areaId: Int?): Measure  {
        val measure = Measure(deviceMeasure.timestamp, deviceMeasure.value, deviceMeasure.sensorId, areaId)
        measureRepository.save(measure)
        return measure
    }
}
