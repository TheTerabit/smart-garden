package pl.put.smartgarden.domain.user

import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.exception.UserAlreadyExistsException
import pl.put.smartgarden.domain.user.repository.UserRepository
import spock.lang.Specification

class UserServiceTest extends Specification {

    def authService = Mock(UserAuthService)
    def deviceService = Mock(UserDeviceService)
    def userRepository = Mock(UserRepository)

    def userService = new UserService(authService, deviceService, userRepository)

    def "Should sign up user if not exists"() {
        given:
        def userDto = new UserSignUpRequest("username", "email@spam.com", "password", "deviceGuid", 16.0, 52.1)

        and:
        userRepository.findByEmail("email@spam.com") >> null
        userRepository.findByUsername("username") >> null

        and:
        def user = new User("username", "email@spam.com", "encryptedPassword", false, null)
        authService.createUser(userDto) >> user

        and:
        def userWithId = new User("username", "email@spam.com", "encryptedPassword", false, null)
        userWithId.id = 75
        userRepository.save(user) >> userWithId

        when:
        userService.signUpUser(userDto)

        then:
        1 * deviceService.createAndSaveDevice(userDto.deviceGuid, userDto.latitude, userDto.longitude, userWithId.id)
        1 * authService.sendVerificationEmail(userDto, userWithId)
    }

    def "Should not sign up user if with such email exists"() {
        given:
        def userDto = new UserSignUpRequest("username", "email@spam.com", "password", "deviceGuid", 16.0, 52.1)

        and:
        userRepository.findByEmail("email@spam.com") >> Mock(User)
        userRepository.findByUsername("username") >> null

        when:
        userService.signUpUser(userDto)

        then:
        thrown UserAlreadyExistsException
    }

    def "Should not sign up user if with such username exists"() {
        given:
        def userDto = new UserSignUpRequest("username", "email@spam.com", "password", "deviceGuid", 16.0, 52.1)

        and:
        userRepository.findByEmail("email@spam.com") >> null
        userRepository.findByUsername("username") >> Mock(User)

        when:
        userService.signUpUser(userDto)

        then:
        thrown UserAlreadyExistsException
    }

    def "Should generate token on login if credentials are correct"() {
        given:
        def userDto = new UserSignInRequest("email@spam.com", "simplePassword")
        def enabledUser = new User("username", "email@spam.com", "encryptedPassword", true, null)
        userRepository.findByEmail("email@spam.com") >> enabledUser
        authService.isUserPasswordCorrect(userDto.password, enabledUser.password) >> true
        authService.generateJsonWebTokenFromUser(enabledUser) >> "jwt-token-generated-correctly"

        when:
        def response = userService.signIn(userDto)

        then:
        response == new UserSignInResponse("jwt-token-generated-correctly", "username")
    }

    def "Should not generate token on login if credentials are incorrect"() {
        given:
        def userDto = new UserSignInRequest("email@spam.com", "simplePassword")
        def enabledUser = new User("username", "email@spam.com", "encryptedPassword", true, null)
        userRepository.findByEmail("email@spam.com") >> enabledUser
        authService.isUserPasswordCorrect(userDto.password, enabledUser.password) >> false
        authService.generateJsonWebTokenFromUser(enabledUser) >> "jwt-token-generated-correctly"

        when:
        userService.signIn(userDto)

        then:
        thrown SmartGardenException
    }

    def "Should not generate token on login if user does not exist"() {
        given:
        def userDto = new UserSignInRequest("email@spam.com", "simplePassword")
        def enabledUser = new User("username", "email@spam.com", "encryptedPassword", true, null)
        enabledUser.id = 123
        userRepository.findByEmail("email@spam.com") >> null
        authService.isUserPasswordCorrect(userDto.password, enabledUser.password) >> true
        authService.generateJsonWebTokenFromUser(enabledUser) >> "jwt-token-generated-correctly"

        when:
        userService.signIn(userDto)

        then:
        thrown SmartGardenException
    }

    def "Should not generate token on login if user is not enabled"() {
        given:
        def userDto = new UserSignInRequest("email@spam.com", "simplePassword")
        def notEnabledUser = new User("username", "email@spam.com", "encryptedPassword", false, null)
        userRepository.findByEmail("email@spam.com") >> notEnabledUser
        authService.isUserPasswordCorrect(userDto.password, notEnabledUser.password) >> true
        authService.generateJsonWebTokenFromUser(notEnabledUser) >> "jwt-token-generated-correctly"

        when:
        userService.signIn(userDto)

        then:
        thrown SmartGardenException
    }

    def "Should enable user if email verification token is correct"() {
        given:
        def emailVerificationToken = "email-verification-token"
        def userToBeEnabled = new User("username", "email@spam.com", "encryptedPassword", false, null)
        authService.getUserFromVerificationToken(emailVerificationToken) >> userToBeEnabled

        when:
        userService.enableUserIfValid(emailVerificationToken)

        then:
        userToBeEnabled.enabled
        1 * userRepository.save(userToBeEnabled)
    }

    def "Should not enable user if email verification token is incorrect"() {
        given:
        def emailVerificationToken = "email-verification-token"
        authService.getUserFromVerificationToken(emailVerificationToken) >> {User -> throw new SmartGardenException() }

        when:
        userService.enableUserIfValid(emailVerificationToken)

        then:
        thrown SmartGardenException
    }

    def "Should retrieve user general settings"() {
        given:
        def user = new User("username", "email@spam.com", "encryptedPassword", true, null)
        user.id = 123
        def device = new Device("deviceGuid", 123, 12.1, 55.3)
        user.device = device

        userRepository.findById(123) >> Optional.of(user)

        when:
        def result = userService.getUserGeneralSettings(123)

        then:
        result == new UserGeneralSettingsResponse("username", "email@spam.com", "deviceGuid", 12.1, 55.3)
    }

    def "Should sign user out"() {
        given:
        def token = "token-to-revoke"

        when:
        userService.signOut(token)

        then:
        1 * authService.revokeToken(token)
    }
}
