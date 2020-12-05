package pl.put.smartgarden.infra.api

import pl.put.smartgarden.domain.user.UserDeviceService
import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.request.UserChangePasswordRequest
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import spock.lang.Specification

class UserGeneralSettingsControllerTest extends Specification {
    def userService = Mock(UserService)
    def userDeviceService = Mock(UserDeviceService)

    def userGeneralSettingsController = new UserGeneralSettingsController(userService, userDeviceService)

    def "Should return currently logged in user"() {
        given:
        def userGeneralSettings = new UserGeneralSettingsResponse("username", "email@mail.com", "guid", 23.3, 54.6)
        userService.getUserGeneralSettings(15) >> userGeneralSettings

        when:
        def user = userGeneralSettingsController.getCurrentUser(15)

        then:
        userGeneralSettings == user
        userGeneralSettings.username == "username"
        userGeneralSettings.email == "email@mail.com"
        userGeneralSettings.deviceGuid == "guid"
        userGeneralSettings.latitude == 23.3
        userGeneralSettings.longitude == 54.6
    }

    def "Should be able to change password"() {
//        given:
//        def request = new UserChangePasswordRequest("oldPassword", "newPassword", "newPassword")
//        userService.changeUserPassword(13, request) >>
//
//        when:
//        userGeneralSettingsController.changePassword(13, request)
    }

    def "ChangeEmail"() {
    }

    def "ChangeUsername"() {
    }

    def "ChangeLocation"() {
    }

    def "GetUserService"() {
    }

    def "GetUserDeviceService"() {
    }
}
