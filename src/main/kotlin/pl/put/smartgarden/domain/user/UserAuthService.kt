package pl.put.smartgarden.domain.user

import io.jsonwebtoken.Jwts
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.SecurityService
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.repository.RevokedTokenRepository
import pl.put.smartgarden.domain.user.repository.UserRepository
import pl.put.smartgarden.domain.user.repository.VerificationTokenRepository

@Service
class UserAuthService(
    val mailService: MailService,
    val userRepository: UserRepository,
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

    /**
     * Checks if token is valid and returns logged in user or throws appropriate exception.
     */
    fun getUserFromJWToken(token: String): User {
        val tokenValue = if (token.startsWith("Bearer ")) token.substring(7) else token

        if (revokedTokenRepository.existsById(tokenValue))
            throw SmartGardenException("Bad token", HttpStatus.UNAUTHORIZED)

        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(tokenValue).body
        val userId = claims["sub"].toString().toIntOrNull()
        userId ?: throw SmartGardenException("Bad token", HttpStatus.UNAUTHORIZED)

        val user = userRepository.findById(userId)

        if (!user.isPresent) throw SmartGardenException("Bad token", HttpStatus.UNAUTHORIZED)

        return user.get()
    }

    fun generateJsonWebTokenFromUser(user: User): String =
        securityService.generateJsonWebTokenFromId(user.id)

    fun revokeToken(token: String) {
        if (!revokedTokenRepository.existsById(token))
            revokedTokenRepository.save(RevokedToken(token))
    }
}