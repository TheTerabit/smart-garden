package pl.put.smartgarden.infra.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.*

@Service
class MailService @Autowired constructor(private val javaMailSender: JavaMailSender) {

    @Value("\${server.contextPath}")
    private val context: String? = null

    /**
     * returns generated verification token.
     */
    fun sendVerificationEmail(username: String, to: String): String {
        val simpleMailMessage = SimpleMailMessage()
        val token = UUID.randomUUID().toString()

        // TODO Change link to something with frontend
        val confirmationUrl = "$context/user/registration-confirmation?token=$token"
        val message = "Hello $username!\nTo complete your registration click in following link:\r\n$confirmationUrl\n\nIf it wasn't you, please ignore this email."

        simpleMailMessage.setTo(to)
        simpleMailMessage.setSubject("SMART GARDEN Account created, confirm your email.")
        simpleMailMessage.setText(message)
        javaMailSender.send(simpleMailMessage)

        return token
    }

}