package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.device.Measure

@Repository
interface MeasureRepository : JpaRepository<Measure, Int>