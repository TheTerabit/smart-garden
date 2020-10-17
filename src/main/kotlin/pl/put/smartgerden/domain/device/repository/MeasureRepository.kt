package pl.put.smartgerden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgerden.domain.device.Measure

@Repository
interface MeasureRepository : JpaRepository<Measure, String>