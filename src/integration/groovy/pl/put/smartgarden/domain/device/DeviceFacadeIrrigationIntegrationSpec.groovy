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

       // linkSensorToArea(userId, areaId, linkSensorRequest: LinkSensorRequest): SimpleAreaResponse {


            def measureTime = Instant.now()
        List<MeasureRequest> measures = [
            new MeasureRequest(measureTime, 3, 1000),
            new MeasureRequest(measureTime, 3, 800),
            new MeasureRequest(measureTime, 2, 900),
            new MeasureRequest(measureTime, 2, 700),
            new MeasureRequest(measureTime, 4, 750),
            new MeasureRequest(measureTime, 4, 750),
            new MeasureRequest(measureTime, 5, 2000),
            new MeasureRequest(measureTime, 5, 2000),
            new MeasureRequest(measureTime, 6, 2000),
            new MeasureRequest(measureTime, 6, 2000),
            new MeasureRequest(measureTime, 7, 750),
            new MeasureRequest(measureTime, 7, 750),
        ]
        deviceFacade.createMeasures(measures, token)
    }

    @Unroll
    def "should irrigate properly"() {
        given:
        areaSettingsRepository.save(areaSettings)
        Area area = new Area(areaSettings, 1, [], [], [])
        areaRepository.save(area)

        def savedSensors = sensorRepository.findAllByDeviceId(1)
        savedSensors.each {it.areaId = area.id}
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
        new AreaSettings(1, 3, TimeUnit.HOUR, 50, 0, false, false, true) | 68
        new AreaSettings(1, 3, TimeUnit.HOUR, 50, 1000, false, true, false) | 68
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
