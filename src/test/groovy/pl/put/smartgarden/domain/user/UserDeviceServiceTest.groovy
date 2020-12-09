package pl.put.smartgarden.domain.user

import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.repository.UserRepository
import spock.lang.Specification

class UserDeviceServiceTest extends Specification {
    def deviceRepository = Mock(DeviceRepository)
    def userRepository = Mock(UserRepository)

    def userDeviceService = new UserDeviceService(deviceRepository, userRepository)

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

    def "Should save device"() {
        given:
        def device = new Device("deviceGuid", 10, 12.3, 45.6)
        deviceRepository.save(device) >> device

        when:
        def result = userDeviceService.saveDevice(device)

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
}
