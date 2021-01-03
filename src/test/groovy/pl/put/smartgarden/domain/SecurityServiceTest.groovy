package pl.put.smartgarden.domain

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.put.smartgarden.domain.user.repository.RevokedTokenRepository
import spock.lang.Specification

class SecurityServiceTest extends Specification {

    def revokedTokenRepository = Mock(RevokedTokenRepository)
    String secretKey = "test-secret"
    SecurityService securityService = new SecurityService(revokedTokenRepository, new BCryptPasswordEncoder(), secretKey)

    def "should get correct id from token"() {
        given:
        def token = "Bearer " + securityService.generateJWTWithIdAndRole(1, ServiceRole.USER)

        when:
        def result = securityService.getIdFromToken(token)

        then:
        result == 1
    }
}
