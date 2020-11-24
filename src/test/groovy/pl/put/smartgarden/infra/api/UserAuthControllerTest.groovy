package pl.put.smartgarden.infra.api


import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import spock.lang.Specification

class UserAuthControllerTest extends Specification {

    def userService = Mock(UserService)
    def userAuthController = new UserAuthController(userService)

    def "Should sign up user on request"() {
        given:
        def userSignUpRequest = new UserSignUpRequest("username", "email@spam.com", "password", "deviceGuid", 16.0, 52.1)

        when:
        userAuthController.signUp(userSignUpRequest)

        then:
        1 * userService.signUpUser(userSignUpRequest)
    }

    def "Should enable user on confirmation"() {
        given:
        def token = "email-verification-token"

        when:
        userAuthController.signUpConfirmation(token)

        then:
        1 * userService.enableUserIfValid(token)
    }

    def "Should sign user in"() {
        given:
        def userSignInRequest = new UserSignInRequest("email@spam.com", "password")
        def userSignInResponse = new UserSignInResponse("jwt-token", "email@spam.com")

        and:
        userService.signIn(userSignInRequest) >> userSignInResponse

        when:
        def response = userAuthController.signIn(userSignInRequest)

        then:
        response == userSignInResponse
    }

    def "Should sign user out"() {
        given:
        def token = "jwt-token-to-revoke"

        when:
        userAuthController.signOut(token)

        then:
        1 * userService.signOut(token)
    }
}
