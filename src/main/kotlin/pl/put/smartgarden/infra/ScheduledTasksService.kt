package pl.put.smartgarden.infra

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.put.smartgarden.domain.device.repository.MeasureRepository

@Transactional
@Service
class ScheduledTasksService(
    val measureRepository: MeasureRepository
) {
    /** Clear measures with null area id, every hour. */
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    fun clearMeasures()
    {
        measureRepository.removeAllByNullAreaId()
    }
}