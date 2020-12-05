package pl.put.smartgarden.domain.user

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.SecurityService
import pl.put.smartgarden.domain.ServiceRole
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.repository.RevokedTokenRepository
import pl.put.smartgarden.domain.user.repository.UserRepository
import pl.put.smartgarden.domain.user.repository.VerificationTokenRepository

@Service
class UserAuthService(
    val mailService: MailService,
    val verificationTokenRepository: VerificationTokenRepository,
    val revokedTokenRepository: RevokedTokenRepository,
    val securityService: SecurityService,
    @Value("\${jwt-secret-key}") val secretKey: String,
    @Value("\${is-user-enabled-by-default}") val isUserEnabledByDefault: Boolean
) {

    fun createUser(userDto: UserSignUpRequest): User = User(
        username = userDto.username,
        enabled = isUserEnabledByDefault,
        email = userDto.email,
        password = securityService.encodePassword(userDto.password)
    )

    fun sendVerificationEmail(userDto: UserSignUpRequest, user: User) {
        if (!isUserEnabledByDefault)
            GlobalScope.async {
                val token = mailService.sendVerificationEmail(userDto.username, userDto.email)
                val verificationToken = VerificationToken(token = token, user = user)
                verificationTokenRepository.save(verificationToken)
            }
    }

    fun isUserPasswordCorrect(password: String, passwordEncoded: String): Boolean {
        return securityService.isPasswordMatching(password, passwordEncoded)
    }

    fun getUserFromVerificationToken(token: String): User {
        val verificationToken = verificationTokenRepository.findByToken(token)
        verificationToken ?: throw SmartGardenException("Invalid token", HttpStatus.BAD_REQUEST)
        return verificationToken.user
    }

    fun generateJsonWebTokenFromUser(user: User): String =
        securityService.generateJWTWithIdAndRole(user.id, ServiceRole.USER)

    fun revokeToken(token: String) {
        if (!revokedTokenRepository.existsById(token))
            revokedTokenRepository.save(RevokedToken(token))
    }

    fun updateUserPassword(user: User, password: String) : User {
        val encodedPassword = securityService.encodePassword(password)
        user.password = encodedPassword

        return user
    }
}