package pl.put.smartgarden.domain.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import pl.put.smartgarden.domain.device.AreaSettings

interface AreaSettingsRepository : JpaRepository<AreaSettings, Int> {
}