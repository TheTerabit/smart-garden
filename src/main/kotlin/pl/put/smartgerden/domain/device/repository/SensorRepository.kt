package pl.put.smartgerden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgerden.domain.device.Sensor

@Repository
interface SensorRepository : JpaRepository<Sensor, String>