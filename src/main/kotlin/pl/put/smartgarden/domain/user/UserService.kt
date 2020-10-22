package pl.put.smartgarden.domain.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.user.dto.UserResourceDto
import pl.put.smartgarden.domain.user.dto.UserSignInDto
import pl.put.smartgarden.domain.user.dto.UserSignInResponseDto
import pl.put.smartgarden.domain.user.dto.UserSignUpDto
import pl.put.smartgarden.infra.exception.SmartGardenException
import pl.put.smartgarden.infra.exception.UserAlreadyExistsException
import pl.put.smartgarden.infra.service.MailService
import java.util.*
import java.util.stream.Collectors


@Service
class UserService(
        val mailService: MailService,
        val userRepository: UserRepository,
        val verificationTokenRepository: VerificationTokenRepository,
        val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    @Value("\${jwt-secret-key}")
    lateinit var secretKey: String;

    @Value("\${use-email-verification}")
    lateinit var useEmailVerification: String

    fun getUsers(): List<UserResourceDto> = userRepository.findAll()
            .stream()
            .map { user ->
                UserResourceDto(
                        username = user.username,
                        email = user.email,
                        deviceGuid = user.device?.guid
                )
            }
            .collect(Collectors.toList())

    @Throws(UserAlreadyExistsException::class)
    fun signUpUser(userDto: UserSignUpDto) {
        if (isUserUnique(userDto)) {
            val user = User(
                    username = userDto.username,
                    enabled = useEmailVerification == "false",
                    email = userDto.email,
                    password = bCryptPasswordEncoder.encode(userDto.password))

            userRepository.save(user)

            if (useEmailVerification == "false")
                GlobalScope.async {
                    val token = mailService.sendVerificationEmail(userDto.username, userDto.email)
                    val verificationToken = VerificationToken(token = token, user = user)
                    verificationTokenRepository.save(verificationToken)
                }


            return
        }

        throw UserAlreadyExistsException("User with this name or email already exists.", HttpStatus.CONFLICT)
    }

    private fun isUserUnique(userDto: UserSignUpDto): Boolean {
        return userRepository.findByEmail(userDto.email) == null && userRepository.findByUsername(userDto.username) == null
    }

    @Throws(SmartGardenException::class)
    fun enableUserIfValid(token: String) {
        val verificationToken = verificationTokenRepository.findByToken(token)
        if (verificationToken != null) {
            val user = verificationToken.user
            user.enabled = true
            userRepository.save(user)
            return
        }

        throw SmartGardenException("Invalid token", HttpStatus.BAD_REQUEST)
    }

    @Throws(SmartGardenException::class)
    fun signIn(user: UserSignInDto): UserSignInResponseDto {
        val userByEmail = userRepository.findByEmail(user.email)

        if (userByEmail != null) {
            if (userByEmail.enabled) {
                if (bCryptPasswordEncoder.matches(user.password, userByEmail.password)) {
                    return UserSignInResponseDto(token = generateJsonWebToken(userByEmail), username = userByEmail.username, id = userByEmail.id!!)
                }
                throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
            }
            throw SmartGardenException("Account is not enabled.", HttpStatus.UNAUTHORIZED)
        }
        throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
    }

    fun getCurrentUser(token: String): UserResourceDto {
        val user = getUserFromToken(token.substring(7))
        return UserResourceDto(username = user.username, email = user.email, deviceGuid = user.device?.guid)
    }

    @Throws(SmartGardenException::class)
    fun getUserFromToken(token: String): User {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

        val user = userRepository.findById(claims["sub"].toString())
        if (user.isPresent) {
            return user.get()
        }

        throw SmartGardenException("Bad token", HttpStatus.UNAUTHORIZED)
    }

    private fun generateJsonWebToken(userByEmail: User): String {
        val now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userByEmail.id)
                .claim("roles", "USER")
                .setIssuedAt(Date(now))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }
}