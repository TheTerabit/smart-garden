package pl.put.smartgarden.infra.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfiguration {

    @Bean
    fun javaMailSender(@Value("emailPassword") password : String): JavaMailSender? {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.gmail.com"
        mailSender.port = 25
        mailSender.username = "apophis131@gmail.com"
        mailSender.password = password
        val props: Properties = mailSender.javaMailProperties

        props.put("mail.transport.protocol", "smtp")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.debug", "true")
        return mailSender
    }
}