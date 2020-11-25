package pl.put.smartgarden.domain.user

import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import spock.lang.Specification

class UserDeviceServiceTest extends Specification {
    def deviceRepository = Mock(DeviceRepository)
    def authService = Mock(UserAuthService)

    def userDeviceService = new UserDeviceService(deviceRepository, authService)

    def "Should be able to create and save device "() {
        given:
        def deviceGuid = "deviceGuid"
        def latitude = 12.3
        def longitude = 52.5
        def userId = 9

        when:
        userDeviceService.createAndSaveDevice(deviceGuid, latitude, longitude, userId)

        then:
        1 * deviceRepository.save({
                    it.getClass() == Device &&
                    it.guid == deviceGuid &&
                    it.latitude == latitude &&
                    it.longitude == longitude &&
                    it.userId == userId
        })
    }

    def "Should save device"() {
        given:
        def device = new Device("deviceGuid", 10, 12.3, 45.6)

        when:
        userDeviceService.saveDevice(device)

        then:
        1 * deviceRepository.save(device)
    }

    def "Should save device location"() {
        given:
        def userToken = "user-jwt"
        def device = new Device("guid", 10, 12.3, 45.6)
        def user = new User("username", "email@mail.com", "encodedPassword", true, device)
        user.id = 10
        authService.getUserFromJWToken(userToken) >> user

        and:
        def latitude = 30.2
        def longitude = 64.5
        def locationRequest = new LocationRequest(latitude, longitude)

        when:
        userDeviceService.setDeviceLocation(userToken, locationRequest)

        then:
        1 * deviceRepository.save(device) >> device
        device.latitude == latitude
        device.longitude == longitude
    }
}
