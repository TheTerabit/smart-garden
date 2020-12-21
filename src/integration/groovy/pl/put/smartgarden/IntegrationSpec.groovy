package pl.put.smartgarden

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import pl.put.smartgarden.domain.device.DeviceFacade
import pl.put.smartgarden.domain.device.repository.AreaRepository
import pl.put.smartgarden.domain.device.repository.AreaSettingsRepository
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import pl.put.smartgarden.domain.device.repository.MeasureRepository
import pl.put.smartgarden.domain.device.repository.SensorRepository
import pl.put.smartgarden.domain.user.repository.UserRepository
import spock.lang.Specification

@SpringBootTest(classes = [SmartGardenApplication])
@ActiveProfiles("integration")
@TestPropertySource(locations="classpath:application-integration.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationSpec extends Specification {

    @Autowired
    DeviceFacade deviceFacade

    @Autowired
    DeviceRepository deviceRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    AreaRepository areaRepository

    @Autowired
    SensorRepository sensorRepository

    @Autowired
    AreaSettingsRepository areaSettingsRepository

    @Autowired
    MeasureRepository measureRepository

}
