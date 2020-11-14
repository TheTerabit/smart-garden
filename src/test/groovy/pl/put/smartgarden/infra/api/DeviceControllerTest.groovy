package pl.put.smartgarden.infra.api

import pl.put.smartgarden.domain.device.DeviceFacade
import spock.lang.Specification

class DeviceControllerTest extends Specification {

    DeviceFacade deviceFacade = Mock(DeviceFacade)
    DeviceController deviceController = new DeviceController(deviceFacade)

    def "should return empty list"() {
        given:
        deviceFacade.getDevices() >> []

        when:
        def result = deviceController.getDevices()

        then:
        result == []
    }
}
