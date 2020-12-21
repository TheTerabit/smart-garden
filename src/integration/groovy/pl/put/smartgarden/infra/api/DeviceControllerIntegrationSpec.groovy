package pl.put.smartgarden.infra.api

import org.springframework.beans.factory.annotation.Autowired
import pl.put.smartgarden.IntegrationSpec

class DeviceControllerIntegrationSpec extends IntegrationSpec {

    @Autowired
    private DeviceController deviceController

    def "when context is loaded then all expected beans created"() {
        expect:
        deviceController
    }
}
