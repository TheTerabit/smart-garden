package pl.put.smartgarden.domain.user

import pl.put.smartgarden.domain.device.*
import pl.put.smartgarden.domain.device.repository.*
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.repository.UserRepository
import spock.lang.Specification

import java.time.Instant

class UserDeviceServiceTest extends Specification {
    def deviceRepository = Mock(DeviceRepository)
    def userRepository = Mock(UserRepository)
    def areaRepository = Mock(AreaRepository)
    def sensorRepository = Mock(SensorRepository)
    def settingsRepository = Mock(AreaSettingsRepository)
    def measuresRepository = Mock(MeasureRepository)
    def irrigationRepository = Mock(IrrigationRepository)

    def userDeviceService = new UserDeviceService(deviceRepository, userRepository, areaRepository, sensorRepository, settingsRepository, measuresRepository, irrigationRepository)

    def "Should be able to create and save device "() {
        given:
        def deviceGuid = "deviceGuid"
        def latitude = 12.3
        def longitude = 52.5
        def userId = 9
        def device = new Device(deviceGuid, userId, latitude, longitude)
        deviceRepository.save({
            it.getClass() == Device &&
                    it.guid == deviceGuid &&
                    it.latitude == latitude &&
                    it.longitude == longitude &&
                    it.userId == userId
        }) >> device

        when:
        def result = userDeviceService.createAndSaveDevice(deviceGuid, latitude, longitude, userId)

        then:
        result == device
    }

    def "Should save device location"() {
        given:
        def device = new Device("guid", 10, 12.3, 45.6)
        def user = new User("username", "email@mail.com", "encodedPassword", true, device)
        user.id = 10
        userRepository.findById(10) >> Optional.of(user)

        and:
        def latitude = 30.2
        def longitude = 64.5
        def locationRequest = new LocationRequest(latitude, longitude)

        when:
        userDeviceService.setDeviceLocation(10, locationRequest)

        then:
        1 * deviceRepository.save(device) >> device
        device.latitude == latitude
        device.longitude == longitude
    }

    def "Should get measures of the selected area"() {
        given: "User connected to a device, with two areas"
        def device = new Device("guid", 213, 12.34, 21.53)
        device.id = 5645
        def user = new User("username", "email", "password", true, device)
        user.id = 213
        userRepository.findById(213) >> Optional.of(user)

        and: "3 sensors linked with one area"
        def sensor1 = new Sensor(SensorType.HUMIDITY, "humidityGuid", 5645)
        sensor1.id = 1
        sensor1.measures = Arrays.asList(
                new Measure(Instant.ofEpochSecond(1234567), 12, 1, 1),
                new Measure(Instant.ofEpochSecond(1235577), 15, 1, 1),
                new Measure(Instant.ofEpochSecond(1236587), 16, 1, 1),
                new Measure(Instant.ofEpochSecond(1237597), 17, 1, 1),
                new Measure(Instant.ofEpochSecond(1238667), 20, 1, 1))
        def sensor2 = new Sensor(SensorType.HUMIDITY, "humidityGuid2", 5645)
        sensor2.id = 2
        sensor2.measures = Arrays.asList(
                new Measure(Instant.ofEpochSecond(1234567), 22, 2, 1),
                new Measure(Instant.ofEpochSecond(1236577), 35, 2, 1),
                new Measure(Instant.ofEpochSecond(1235587), 46, 2, 1),
                new Measure(Instant.ofEpochSecond(1237597), 57, 2, 1),
                new Measure(Instant.ofEpochSecond(1238668), 60, 2, 1))
        def sensor3 = new Sensor(SensorType.ILLUMINANCE, "illuminanceGuid", 5645)
        sensor3.id = 3
        sensor3.measures = Arrays.asList(
                new Measure(Instant.ofEpochSecond(1234567), 345, 3, 1),
                new Measure(Instant.ofEpochSecond(12678690), 444, 3, 1))

        and: "1 sensor linked with other area"
        def sensor4 = new Sensor(SensorType.TEMPERATURE, "temperatureGuid", 5645)
        sensor4.id = 4
        sensor4.measures = Arrays.asList(
                new Measure(Instant.ofEpochSecond(1234500), 22, 4, 2),
                new Measure(Instant.ofEpochSecond(1234600), 35, 4, 2),
                new Measure(Instant.ofEpochSecond(1234700), 46, 4, 2))

        and: "1 sensor linked with completely different area and device"
        def sensor5 = new Sensor(SensorType.TEMPERATURE, "Connected to different device", 100000)
        sensor5.id = 5
        sensor5.measures = Arrays.asList(
                new Measure(Instant.ofEpochSecond(1234500), 22, 5, 7),
                new Measure(Instant.ofEpochSecond(1234600), 35, 5, 7),
                new Measure(Instant.ofEpochSecond(1234700), 46, 5, 7))


        def irrigation1 = new Irrigation(Instant.ofEpochSecond(1234400), 1, 500)
        def area1 = new Area(new AreaSettings(), 5645, Arrays.asList(sensor1, sensor2, sensor3), sensor1.measures + sensor2.measures + sensor3.measures, Arrays.asList(irrigation1))
        area1.id = 1
        area1.settings.areaId = area1.id
        sensor1.areaId = 1
        sensor2.areaId = 1
        sensor3.areaId = 1

        def area2 = new Area(new AreaSettings(), 5645, Arrays.asList(sensor4), sensor4.measures, Collections.emptyList())
        area2.id = 2
        area2.settings.areaId = area2.id
        sensor4.areaId = 2

        device.areas = Arrays.asList(area1, area2)
        device.sensors = Arrays.asList(sensor1, sensor2, sensor3, sensor4)

        measuresRepository.findMeasures(5645, 1, SensorType.HUMIDITY, Instant.ofEpochSecond(0), Instant.ofEpochSecond(99999999999)) >> sensor1.measures + sensor2.measures
        measuresRepository.findMeasures(5645, 1, SensorType.ILLUMINANCE, Instant.ofEpochSecond(0), Instant.ofEpochSecond(99999999999)) >> sensor3.measures
        measuresRepository.findMeasures(5645, 1, SensorType.TEMPERATURE, Instant.ofEpochSecond(0), Instant.ofEpochSecond(99999999999)) >> Collections.emptyList()

        measuresRepository.findMeasures(5645, 2, SensorType.HUMIDITY, Instant.ofEpochSecond(1234550), Instant.ofEpochSecond(1234650)) >> Collections.emptyList()
        measuresRepository.findMeasures(5645, 2, SensorType.ILLUMINANCE, Instant.ofEpochSecond(1234550), Instant.ofEpochSecond(1234650)) >> Collections.emptyList()
        measuresRepository.findMeasures(5645, 2, SensorType.TEMPERATURE, Instant.ofEpochSecond(1234550), Instant.ofEpochSecond(1234650)) >> Collections.singletonList(sensor4.measures[1])

        irrigationRepository.getLastIrrigation(1) >> Collections.singletonList(new Irrigation(Instant.ofEpochSecond(1234550), 1, 0))
        irrigationRepository.getLastIrrigation(2) >> Collections.singletonList(new Irrigation(Instant.ofEpochSecond(1234560), 2, 0))

        sensorRepository.findAllByAreaIdAndDeviceId(null, 5645) >> Collections.emptyList()

        when: "Retrieving all area measures from first area"
        def areaResponse = userDeviceService.getAreaMeasures(213, 1, Instant.ofEpochSecond(0), Instant.ofEpochSecond(99999999999))

        then: "Only measures from sensors connected to first area should be retrieved"
        areaResponse.id == 1
        areaResponse.humidity == 30 // average of last measures
        areaResponse.humidityMeasures.size() == 1

        areaResponse.illuminance == 444
        areaResponse.illuminanceMeasures.size() == 2

        areaResponse.temperature == 0 // there are no measures
        areaResponse.temperatureMeasures.size() == 0

        when: "Retrieving area measures within time range from second area"
        def areaResponse2 = userDeviceService.getAreaMeasures(213, 2, Instant.ofEpochSecond(1234550), Instant.ofEpochSecond(1234650))

        then: "There should be only one measure within given range"
        areaResponse2.id == 2
        areaResponse2.humidity == 0
        areaResponse2.humidityMeasures.size() == 0

        areaResponse2.illuminance == 0
        areaResponse2.illuminanceMeasures.size() == 0

        areaResponse2.temperature == 35 // This is last average from given range temperature
        areaResponse2.temperatureMeasures.size() == 1 // Other two are filtered out by date range
        areaResponse2.temperatureMeasures[0].value == 35 // Other two are filtered out by date range
    }
}
