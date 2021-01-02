package pl.put.smartgarden.domain

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.user.repository.RevokedTokenRepository
import java.util.Date

@Service
class SecurityService(
    val revokedTokenRepository: RevokedTokenRepository,
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    @Value("\${jwt-secret-key}") val secretKey: String
) {
    fun encodePassword(password: String) : String =
        bCryptPasswordEncoder.encode(password)

    fun isPasswordMatching(rawPassword: String, encodedPassword: String) =
        bCryptPasswordEncoder.matches(rawPassword, encodedPassword)

    fun generateJWTWithIdAndRole(id: Int, role: ServiceRole): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setSubject(id.toString())
            .claim("role", role.name)
            .setIssuedAt(Date(now))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun isTokenRevoked(token: String): Boolean = revokedTokenRepository.existsById(token)

    fun getIdFromToken(token: String): Int =
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.substring(7, token.length)).body["sub"].toString().toInt()
}