package pl.put.smartgarden.domain

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date

@Service
class SecurityService(
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    @Value("\${jwt-secret-key}") val secretKey: String
) {
    fun encodePassword(password: String) =
        bCryptPasswordEncoder.encode(password)

    fun isPasswordMatching(password1: String, password2: String) =
        bCryptPasswordEncoder.matches(password1, password2)

    fun generateJsonWebTokenFromId(id: Int): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setSubject(id.toString())
            .claim("roles", "USER")
            .setIssuedAt(Date(now))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun getIdFromToken(token: String): Int =
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body["sub"].toString().toInt()
}