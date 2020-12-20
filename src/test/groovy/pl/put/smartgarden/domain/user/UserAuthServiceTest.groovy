package pl.put.smartgarden.domain.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import pl.put.smartgarden.domain.SecurityService
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.repository.RevokedTokenRepository
import pl.put.smartgarden.domain.user.repository.UserRepository
import pl.put.smartgarden.domain.user.repository.VerificationTokenRepository
import spock.lang.Specification

class UserAuthServiceTest extends Specification {
    def mailService = Mock(MailService)
    def userRepository = Mock(UserRepository)
    def verificationTokenRepository = Mock(VerificationTokenRepository)
    def revokedTokenRepository = Mock(RevokedTokenRepository)
    def securityService = Mock(SecurityService)
    def secretKey = "secretKey"
    def userEnabledByDefault = true

    def userAuthService = new UserAuthService(
            mailService,
            verificationTokenRepository,
            revokedTokenRepository,
            securityService,
            secretKey,
            userEnabledByDefault)

    def "Should create user object with encrypted password"() {
        given:
        def userSignUpRequest = new UserSignUpRequest("username", "email@spam.com", "password", "deviceGuid", 16.0, 52.1)
        securityService.encodePassword("password") >> "passwordEncoded"

        when:
        def result = userAuthService.createUser(userSignUpRequest)

        then:
        result.username == "username"
        result.enabled == userEnabledByDefault
        result.email == "email@spam.com"
        result.password == "passwordEncoded"
    }

    def "Should return true if passwords matches encrypted one"() {
        given:
        securityService.isPasswordMatching("password", "passwordEncoded") >> true

        when:
        def result = userAuthService.isUserPasswordCorrect("password", "passwordEncoded")

        then:
        result
    }

    def "Should return false if passwords does not match encrypted one"() {
        given:
        securityService.isPasswordMatching("password", "passwordEncoded") >> false

        when:
        def result = userAuthService.isUserPasswordCorrect("password", "passwordEncoded")

        then:
        !result
    }

    def "Should get user from email verification token"() {
        given:
        def token = "verification-token"
        def user = new User("username", "email@spam.com", "encryptedPassword", true, null)
        def tokenObject = new VerificationToken(token, user)
        verificationTokenRepository.findByToken(token) >> tokenObject

        when:
        def result = userAuthService.getUserFromVerificationToken(token)

        then:
        result == user
    }

    def "Should throw exception when email verification token is wrong"() {
        given:
        def token = "verification-token"
        def user = new User("username", "email@spam.com", "encryptedPassword", true, null)
        verificationTokenRepository.findByToken(token) >> null

        when:
        userAuthService.getUserFromVerificationToken(token)

        then:
        thrown SmartGardenException
    }
}
