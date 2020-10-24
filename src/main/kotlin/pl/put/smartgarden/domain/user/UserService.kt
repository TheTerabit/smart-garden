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
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.user.exception.UserAlreadyExistsException
import java.util.Date


@Service
class UserService(
    val mailService: MailService,
    val userRepository: UserRepository,
    val verificationTokenRepository: VerificationTokenRepository,
    val bCryptPasswordEncoder: BCryptPasswordEncoder,
    @Value("\${jwt-secret-key}") val secretKey: String,
    @Value("\${is-user-enabled-by-default}") val isUserEnabledByDefault: Boolean
) {

    fun getUsers(): List<UserResourceDto> = userRepository.findAll()
        .map { user ->
            UserResourceDto(
                username = user.username,
                email = user.email,
                deviceGuid = user.device?.guid
            )
        }

    fun signUpUser(userDto: UserSignUpDto) =
        if (isUserUnique(userDto)) {
            val user = createUser(userDto)
            sendVerificationEmail(userDto, user)
        } else {
            throw UserAlreadyExistsException("User with this name or email already exists.", HttpStatus.CONFLICT)
        }

    private fun isUserUnique(userDto: UserSignUpDto): Boolean =
        userRepository.findByEmail(userDto.email) == null && userRepository.findByUsername(userDto.username) == null

    private fun createUser(userDto: UserSignUpDto): User {
        val user = User(
            username = userDto.username,
            enabled = isUserEnabledByDefault,
            email = userDto.email,
            password = bCryptPasswordEncoder.encode(userDto.password)
        )
        userRepository.save(user)
        return user
    }

    private fun sendVerificationEmail(userDto: UserSignUpDto, user: User) {
        if (!isUserEnabledByDefault)
            GlobalScope.async {
                val token = mailService.sendVerificationEmail(userDto.username, userDto.email)
                val verificationToken = VerificationToken(token = token, user = user)
                verificationTokenRepository.save(verificationToken)
            }
    }

    fun enableUserIfValid(token: String) {
        val verificationToken = verificationTokenRepository.findByToken(token)
        verificationToken ?: throw SmartGardenException("Invalid token", HttpStatus.BAD_REQUEST)
        val user = verificationToken.user
        user.enabled = true
        userRepository.save(user)
    }

    fun signIn(userSignInDto: UserSignInDto): UserSignInResponseDto {
        var user = userRepository.findByEmail(userSignInDto.email)
        user = validateUserSignIn(user)
        return UserSignInResponseDto(
            token = generateJsonWebToken(user),
            username = user.username,
            id = user.id
        )
    }

    private fun validateUserSignIn(user: User?): User {
        user ?: throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
        if (!user.enabled) throw SmartGardenException("Account is not enabled.", HttpStatus.UNAUTHORIZED)
        if (!bCryptPasswordEncoder.matches(user.password, user.password)) throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
        return user
    }

    fun getCurrentUser(token: String): UserResourceDto {
        val user = getUserFromToken(token.substring(7))
        return UserResourceDto(
            username = user.username,
            email = user.email,
            deviceGuid = user.device?.guid
        )
    }

    private fun getUserFromToken(token: String): User {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

        val user = userRepository.findById(claims["sub"].toString())
        if (user.isPresent) {
            return user.get()
        } else {
            throw SmartGardenException("Bad token", HttpStatus.UNAUTHORIZED)
        }
    }

    private fun generateJsonWebToken(userByEmail: User): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setSubject(userByEmail.id)
            .claim("roles", "USER")
            .setIssuedAt(Date(now))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }
}