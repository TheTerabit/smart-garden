package pl.put.smartgarden.domain.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.repository.RevokedTokenRepository
import pl.put.smartgarden.domain.user.repository.UserRepository
import pl.put.smartgarden.domain.user.repository.VerificationTokenRepository
import java.util.Date

@Service
class SecurityService(
    val mailService: MailService,
    val userRepository: UserRepository,
    val verificationTokenRepository: VerificationTokenRepository,
    val revokedTokenRepository: RevokedTokenRepository,
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    @Value("\${jwt-secret-key}") val secretKey: String,
    @Value("\${is-user-enabled-by-default}") val isUserEnabledByDefault: Boolean
) {

    fun createUser(userDto: UserSignUpRequest): User {
        return User(
            username = userDto.username,
            enabled = isUserEnabledByDefault,
            email = userDto.email,
            password = bCryptPasswordEncoder.encode(userDto.password)
        )
    }

    fun sendVerificationEmail(userDto: UserSignUpRequest, user: User) {
        if (!isUserEnabledByDefault)
            GlobalScope.async {
                val token = mailService.sendVerificationEmail(userDto.username, userDto.email)
                val verificationToken = VerificationToken(token = token, user = user)
                verificationTokenRepository.save(verificationToken)
            }
    }

    fun validateUserSignIn(user: User?): User {
        user ?: throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
        if (!user.enabled) throw SmartGardenException("Account is not enabled.", HttpStatus.UNAUTHORIZED)
        if (!bCryptPasswordEncoder.matches(user.password, user.password)) throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
        return user
    }

    fun getUserFromVerificationToken(token: String): User {
        val verificationToken = verificationTokenRepository.findByToken(token)
        verificationToken ?: throw SmartGardenException("Invalid token", HttpStatus.BAD_REQUEST)
        return verificationToken.user
    }

    fun getUserFromJWToken(token: String): User {
        val tokenValue = if (token.startsWith("Bearer ")) token.substring(7) else token

        if (revokedTokenRepository.existsById(tokenValue))
            throw SmartGardenException("Bad token", HttpStatus.UNAUTHORIZED)

        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(tokenValue).body
        val user = userRepository.findById(claims["sub"].toString())

        if (!user.isPresent) throw SmartGardenException("Bad token", HttpStatus.UNAUTHORIZED)

        return user.get()
    }

    fun generateJsonWebTokenFromUser(user: User): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setSubject(user.id)
            .claim("roles", "USER")
            .setIssuedAt(Date(now))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun revokeToken(token: String) {
        if (!revokedTokenRepository.existsById(token))
            revokedTokenRepository.save(RevokedToken(token))
    }
}