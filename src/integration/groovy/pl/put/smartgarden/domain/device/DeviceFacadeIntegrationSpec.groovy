package pl.put.smartgarden.domain.device

import pl.put.smartgarden.IntegrationSpec
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.request.SensorRequest
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse
import pl.put.smartgarden.domain.user.User

import java.time.Instant

class DeviceFacadeIntegrationSpec extends IntegrationSpec {
    def "should update device when user registered device"() {
        given:
        def device = new Device("123456789", 1, 20.0, 20.0)
        def user = new User("username", "username@gmail.com", "123", true, device)
        deviceRepository.save(device)
        userRepository.save(user)
        def deviceRequest = new DeviceRequest(
            "123456789",
            [
                new SensorRequest(SensorType.IRRIGATION, "11111"),
                new SensorRequest(SensorType.IRRIGATION, "22222"),
                new SensorRequest(SensorType.HUMIDITY, "33333"),
                new SensorRequest(SensorType.HUMIDITY, "44444"),
                new SensorRequest(SensorType.TEMPERATURE, "55555"),
                new SensorRequest(SensorType.TEMPERATURE, "66666"),
                new SensorRequest(SensorType.ILLUMINANCE, "77777"),
            ])

        when:
        DeviceResponse deviceResponse = deviceFacade.createOrUpdateDevice(deviceRequest)

        then:
        notThrown()
    }

    def "should save measures"() {
        given:
        def device = new Device("123456789", 1, 20.0, 20.0)
        def user = new User("username", "username@gmail.com", "123", true, device)
        deviceRepository.save(device)
        userRepository.save(user)
        def deviceRequest = new DeviceRequest(
            "123456789",
            [
                new SensorRequest(SensorType.IRRIGATION, "11111"),
                new SensorRequest(SensorType.IRRIGATION, "22222"),
                new SensorRequest(SensorType.HUMIDITY, "33333"),
                new SensorRequest(SensorType.HUMIDITY, "44444"),
                new SensorRequest(SensorType.TEMPERATURE, "55555"),
                new SensorRequest(SensorType.TEMPERATURE, "66666"),
                new SensorRequest(SensorType.ILLUMINANCE, "77777"),
            ])
        def token = deviceFacade.createOrUpdateDevice(deviceRequest).accessToken
        def measureTime = Instant.now()
        List<MeasureRequest> measures = [new MeasureRequest(measureTime, 3, 1000)]
        deviceFacade.createMeasures(measures, token)

        def sensor1 = new Sensor(SensorType.IRRIGATION, "11111", 1)
        sensor1.id = 1
        def sensor2 = new Sensor(SensorType.IRRIGATION, "22222", 1)
        sensor2.id = 2
        def sensor3 = new Sensor(SensorType.HUMIDITY, "33333", 1)
        sensor3.id = 3
        def sensor4 = new Sensor(SensorType.HUMIDITY, "44444", 1)
        sensor4.id = 4
        def sensor5 = new Sensor(SensorType.TEMPERATURE, "55555", 1)
        sensor5.id = 5
        def sensor6 = new Sensor(SensorType.TEMPERATURE, "66666", 1)
        sensor6.id = 6
        def sensor7 = new Sensor(SensorType.ILLUMINANCE, "77777", 1)
        sensor7.id = 7

        def measure1 = new Measure(measureTime, 1000, 3)
        measure1.id = 1
        sensor3.measures = [measure1]

        device.sensors = [sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7]

        when:
        Device result = deviceRepository.findDeviceByGuid("123456789")


        then:
        notThrown()
        with(result) {
            id == device.id
            areas == device.areas
            sensors[1] == device.sensors[1]
        }
    }
}