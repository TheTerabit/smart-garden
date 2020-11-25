package pl.put.smartgarden.domain.user

import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Ignore
import spock.lang.Specification

class MailServiceTest extends Specification {
    def javaMailSender = Mock(JavaMailSender)

    def mailService = new MailService(javaMailSender)

    // This test is probably not enough, but MailService should be modified to make it testable.
    def "Should send email"() {
        when:
        mailService.sendVerificationEmail("username", "mail@mail.com")

        then:
        1 * javaMailSender.send(_)
    }
}
