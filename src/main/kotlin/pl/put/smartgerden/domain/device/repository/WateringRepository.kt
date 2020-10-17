package pl.put.smartgerden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgerden.domain.device.Watering

@Repository
interface WateringRepository : JpaRepository<Watering, String>