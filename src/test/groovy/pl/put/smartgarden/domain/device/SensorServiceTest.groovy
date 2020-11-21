package pl.put.smartgarden.domain.device

import pl.put.smartgarden.domain.device.dto.request.SensorRequest
import pl.put.smartgarden.domain.device.repository.SensorRepository
import spock.lang.Specification

import java.time.Instant

import static pl.put.smartgarden.domain.device.SensorType.HUMIDITY
import static pl.put.smartgarden.domain.device.SensorType.IRRIGATION
import static pl.put.smartgarden.domain.device.SensorType.TEMPERATURE

class SensorServiceTest extends Specification {
    def sensorRepository = Mock(SensorRepository)
    def sensorService = new SensorService(sensorRepository)

    def "should create sensors"() {
        given:
        def sensorRequest1 = new SensorRequest(HUMIDITY, "x32d327f8h8h2f83hf8923hf9302hf0h239f8h8ehf7g2f4679")
        def sensorRequest2 = new SensorRequest(IRRIGATION, "webfqh8732gf7g28fodh2938dh30h8f3g297gf9732gf723gf72")
        def sensorRequests = [sensorRequest1, sensorRequest2]
        def sensor1 = new Sensor(TEMPERATURE, "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e", 23)
        sensor1.id = 1
        sensor1.areaId = 3
        def measured = [new Measure(Instant.now(), 22.5, 1)]
        sensor1.measures = measured
        def sensor2 = new Sensor(IRRIGATION, "edhh2387gfd782gf7834gf863g467fg67w", 23)
        sensorRepository.findAllByDeviceId(23) >> [sensor1, sensor2]
        sensorRepository.findAllByGuidIn(["x32d327f8h8h2f83hf8923hf9302hf0h239f8h8ehf7g2f4679", "webfqh8732gf7g28fodh2938dh30h8f3g297gf9732gf723gf72"]) >> []

        when:
        List<Sensor> result = sensorService.createSensors(23, sensorRequests)

        then:
        result.size() == 4
        with(result[0]) {
            guid == "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e"
            deviceId == 23
            type == TEMPERATURE
            areaId == 3
            measures == measured
            !isActive
        }
        with(result[1]) {
            guid == "edhh2387gfd782gf7834gf863g467fg67w"
            deviceId == 23
            type == IRRIGATION
            areaId == null
            measures == []
            !isActive
        }
        with(result[2]) {
            guid == "x32d327f8h8h2f83hf8923hf9302hf0h239f8h8ehf7g2f4679"
            deviceId == 23
            type == HUMIDITY
            areaId == null
            measures == []
            isActive
        }
        with(result[3]) {
            guid == "webfqh8732gf7g28fodh2938dh30h8f3g297gf9732gf723gf72"
            deviceId == 23
            type == IRRIGATION
            areaId == null
            measures == []
            isActive
        }
    }

    def "should not create new sensor when guid already in db"() {
        given:
        def sensorRequest1 = new SensorRequest(HUMIDITY, "x32d327f8h8h2f83hf8923hf9302hf0h239f8h8ehf7g2f4679")
        def sensorRequest2 = new SensorRequest(IRRIGATION, "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e")
        def sensorRequests = [sensorRequest1, sensorRequest2]
        def sensor1 = new Sensor(TEMPERATURE, "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e", 23)
        sensor1.id = 1
        sensor1.areaId = 3
        def measured = [new Measure(Instant.now(), 22.5, 1)]
        sensor1.measures = measured
        def sensor2 = new Sensor(IRRIGATION, "edhh2387gfd782gf7834gf863g467fg67w", 23)
        sensorRepository.findAllByDeviceId(23) >> [sensor1, sensor2]
        sensorRepository.findAllByGuidIn(["x32d327f8h8h2f83hf8923hf9302hf0h239f8h8ehf7g2f4679", "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e"]) >> [sensor1]

        when:
        List<Sensor> result = sensorService.createSensors(23, sensorRequests)

        then:
        result.size() == 3
        with(result[0]) {
            guid == "edhh2387gfd782gf7834gf863g467fg67w"
            deviceId == 23
            type == IRRIGATION
            areaId == null
            measures == []
            !isActive
        }
        with(result[1]) {
            guid == "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e"
            deviceId == 23
            type == TEMPERATURE
            areaId == 3
            measures == measured
            isActive
        }
        with(result[2]) {
            guid == "x32d327f8h8h2f83hf8923hf9302hf0h239f8h8ehf7g2f4679"
            deviceId == 23
            type == HUMIDITY
            areaId == null
            measures == []
            isActive
        }
    }

    def "should not create sensors because a sensor already belongs to another device"() {
        given:
        def sensorRequest1 = new SensorRequest(HUMIDITY, "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e")
        def sensorRequest2 = new SensorRequest(IRRIGATION, "edhh2387gfd782gf7834gf863g467fg67w")
        def sensorRequests = [sensorRequest1, sensorRequest2]
        def sensor1 = new Sensor(TEMPERATURE, "w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e", 22)
        sensor1.id = 1
        sensor1.areaId = 3
        def measured = [new Measure(Instant.now(), 22.5, 1)]
        sensor1.measures = measured
        def sensor2 = new Sensor(IRRIGATION, "edhh2387gfd782gf7834gf863g467fg67w", 23)
        sensorRepository.findAllByDeviceId(23) >> [sensor2]
        sensorRepository.findAllByGuidIn(["w4e6r6t6cr5xe4w3z54e65r6t7yt7r56e", "edhh2387gfd782gf7834gf863g467fg67w"]) >> [sensor1, sensor2]

        when:
        sensorService.createSensors(23, sensorRequests)

        then:
        def e = thrown(RuntimeException)
        e.message == "Some sensors are already assigned to another device."
    }

}
