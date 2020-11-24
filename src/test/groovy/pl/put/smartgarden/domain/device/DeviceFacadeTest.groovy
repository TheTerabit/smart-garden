package pl.put.smartgarden.domain.device

import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.SensorRequest
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.SecurityService
import spock.lang.Specification

import static pl.put.smartgarden.domain.device.SensorType.HUMIDITY

class DeviceFacadeTest extends Specification {
    def deviceService = Mock(DeviceService)
    def sensorService = Mock(SensorService)
    def securityService = Mock(SecurityService)
    def measureService = Mock(MeasuseService)
    def deviceFacade = new DeviceFacade(deviceService, sensorService, securityService, measureService)

    def "should successfully update device details"() {
        given:
        new Device()
        def sensorRequest = new SensorRequest(HUMIDITY, "dafergmjuyrhtgrfeadwefshjk")
        def deviceRequest = new DeviceRequest("123456789", [sensorRequest])
        def sensor = new Sensor(HUMIDITY,"dafergmjuyrhtgrfeadwefshjk",23)
        sensor.id = 1
        def sensorResponse = new SensorResponse(1, "dafergmjuyrhtgrfeadwefshjk")
        def device = new Device("dafergmjuyrhtgrfeadwefshjk", 1, 0, 0)
        device.id = 23
        deviceService.getDeviceByGuid(deviceRequest.guid) >> device
        sensorService.createSensors(23, deviceRequest.sensors) >> [sensor]
        securityService.generateJsonWebTokenFromId(23) >> "ef1231313ewfewun21uneu12nu214un421un3u13n2un31u3nu131=="

        when:
        def result = deviceFacade.createOrUpdateDevice(deviceRequest)

        then:
        def deviceResponse = new DeviceResponse("ef1231313ewfewun21uneu12nu214un421un3u13n2un31u3nu131==", [sensorResponse])
        result == deviceResponse
    }
}
