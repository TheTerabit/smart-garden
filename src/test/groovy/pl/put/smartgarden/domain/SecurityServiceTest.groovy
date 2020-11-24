package pl.put.smartgarden.domain

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class SecurityServiceTest extends Specification {

    String secretKey = "test-secret"
    SecurityService securityService = new SecurityService(new BCryptPasswordEncoder(), secretKey)

    def "should get correct id from token"() {
        given:
        def token = securityService.generateJsonWebTokenFromId(1)

        when:
        def result = securityService.getIdFromToken(token)

        then:
        result == 1
    }
}
