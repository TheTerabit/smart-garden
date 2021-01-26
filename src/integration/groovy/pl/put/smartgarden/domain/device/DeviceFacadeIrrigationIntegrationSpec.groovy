package pl.put.smartgarden.domain.device

import pl.put.smartgarden.IntegrationSpec
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.request.SensorRequest
import pl.put.smartgarden.domain.user.User
import spock.lang.Unroll

import java.time.Instant

class DeviceFacadeIrrigationIntegrationSpec extends IntegrationSpec {

    String token
    Integer areaId

    def setup() {
        def device = new Device("123456789", 1, 20.0, 20.0)
        def user = new User("username", "username@gmail.com", "123", true, device)
        deviceRepository.save(device)
        userRepository.save(user)
        def deviceRequest = new DeviceRequest(
            "123456789",
            [
                new SensorRequest(SensorType.IRRIGATION, "11111"),
                new SensorRequest(SensorType.HUMIDITY, "22222"),
                new SensorRequest(SensorType.HUMIDITY, "33333"),
                new SensorRequest(SensorType.HUMIDITY, "44444"),
                new SensorRequest(SensorType.TEMPERATURE, "55555"),
                new SensorRequest(SensorType.TEMPERATURE, "66666"),
                new SensorRequest(SensorType.ILLUMINANCE, "77777"),
            ])
        token = "Bearer " + deviceFacade.createOrUpdateDevice(deviceRequest).accessToken

        def areaSettings = new AreaSettings(1, 0, TimeUnit.HOUR, 0, 0, false, false, false)
        areaSettingsRepository.save(areaSettings)
        Area area = new Area(areaSettings, 1, [], [], [])
        areaRepository.save(area)

            def measureTime = Instant.now()
        List<Measure> measures = [
            new Measure(measureTime, 1000, 3, 1),
            new Measure(measureTime, 800, 3, 1),
            new Measure(measureTime, 900, 2, 1),
            new Measure(measureTime, 700, 2, 1),
            new Measure(measureTime, 750, 4, 1),
            new Measure(measureTime, 750, 4, 1),
            new Measure(measureTime, 30, 5, 1),
            new Measure(measureTime, 30, 5, 1),
            new Measure(measureTime, 30, 6, 1),
            new Measure(measureTime, 30, 6, 1),
            new Measure(measureTime, 750, 7, 1),
            new Measure(measureTime, 750, 7, 1)
        ]
        measureRepository.saveAll(measures)
    }

    @Unroll
    def "should irrigate properly"() {
        given:
        areaSettings.id = 1
        areaSettingsRepository.save(areaSettings)

        def savedSensors = sensorRepository.findAllByDeviceId(1)
        savedSensors.each {it.areaId = 1}
        sensorRepository.saveAll(savedSensors)

        when:
        def result = deviceFacade.getIrrigationDecisions(token)

        then:
        with(result[0]) {
            sensorGuid == "11111"
            irrigationTimeInSeconds == expected
        }

        where:
        areaSettings | expected
        new AreaSettings(1, 3, TimeUnit.HOUR, 50, 0, false, false, false) | 0
        new AreaSettings(1, 3, TimeUnit.HOUR, 300, 0, false, false, true) | 305
        new AreaSettings(1, 3, TimeUnit.HOUR, 120, 1000, false, true, false) | 122
    }

    def "should irrigate one time only"() {
        given:
        def areaSettings = new AreaSettings(1, 3, TimeUnit.HOUR, 50, 1000, false, true, false)
        areaSettingsRepository.save(areaSettings)
        Area area = new Area(areaSettings, 1, [], [], [])
        areaRepository.save(area)

        def savedSensors = sensorRepository.findAllByDeviceId(1)
        savedSensors.each { it.areaId = area.id }
        sensorRepository.saveAll(savedSensors)

        deviceFacade.getIrrigationDecisions(token)

        when:
        def result = deviceFacade.getIrrigationDecisions(token)

        then:
        with(result[0]) {
            sensorGuid == "11111"
            irrigationTimeInSeconds == 0
        }
    }

    def "should irrigate one time only when irrigateNow is clicked"() {
        given:
        def areaSettings = new AreaSettings(1, 3, TimeUnit.HOUR, 50, 1000, false, true, true)
        areaSettingsRepository.save(areaSettings)
        Area area = new Area(areaSettings, 1, [], [], [])
        areaRepository.save(area)

        def savedSensors = sensorRepository.findAllByDeviceId(1)
        savedSensors.each { it.areaId = area.id }
        sensorRepository.saveAll(savedSensors)

        deviceFacade.getIrrigationDecisions(token)

        when:
        def result = deviceFacade.getIrrigationDecisions(token)

        then:
        with(result[0]) {
            sensorGuid == "11111"
            irrigationTimeInSeconds == 0
        }
    }
}
