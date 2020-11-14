package pl.put.smartgarden.domain.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.user.dto.request.UserResourceRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponseRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.user.dto.request.AreaRequest
import pl.put.smartgarden.domain.user.dto.request.AreaSettingsRequest
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.exception.UserAlreadyExistsException
import pl.put.smartgarden.domain.user.repository.UserRepository
import pl.put.smartgarden.domain.user.repository.VerificationTokenRepository
import java.time.Instant
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

    fun getUsers(): List<UserResourceRequest> = userRepository.findAll()
        .map { user ->
            UserResourceRequest(
                username = user.username,
                email = user.email,
                deviceGuid = user.device?.guid
            )
        }

    fun signUpUser(userRequest: UserSignUpRequest) =
        if (isUserUnique(userRequest)) {
            val user = createUser(userRequest)
            sendVerificationEmail(userRequest, user)
        } else {
            throw UserAlreadyExistsException("User with this name or email already exists.", HttpStatus.CONFLICT)
        }

    private fun isUserUnique(userRequest: UserSignUpRequest): Boolean =
        userRepository.findByEmail(userRequest.email) == null && userRepository.findByUsername(userRequest.username) == null

    private fun createUser(userRequest: UserSignUpRequest): User {
        val user = User(
            username = userRequest.username,
            enabled = isUserEnabledByDefault,
            email = userRequest.email,
            password = bCryptPasswordEncoder.encode(userRequest.password)
        )
        userRepository.save(user)
        return user
    }

    private fun sendVerificationEmail(userRequest: UserSignUpRequest, user: User) {
        if (!isUserEnabledByDefault)
            GlobalScope.async {
                val token = mailService.sendVerificationEmail(userRequest.username, userRequest.email)
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

    fun signIn(userSignInRequest: UserSignInRequest): UserSignInResponseRequest {
        var user = userRepository.findByEmail(userSignInRequest.email)
        user = validateUserSignIn(user)
        return UserSignInResponseRequest(
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

    fun getCurrentUser(token: String): UserResourceRequest {
        val user = getUserFromToken(token.substring(7))
        return UserResourceRequest(
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

    fun getAreaMeasures(token: String, areaId: String, from: Instant, to: Instant): List<MeasureRequest> {
        TODO("Not yet implemented")
    }

    fun setIrrigationLevel(token: String, areaId: String, irrigationLevelRequest: IrrigationLevelRequest): AreaSettingsRequest {
        TODO("Not yet implemented")
    }

    fun getAreasSetting(token: String): List<AreaSettingsRequest> {
        TODO("Not yet implemented")
    }

    fun setLocation(token: String, locationRequest: LocationRequest): UserResourceRequest {
        TODO("Not yet implemented")
    }

    fun setNextIrrigationTime(token: String, areaId: String, irrigationTimeRequest: NextIrrigationRequest): NextIrrigationRequest {
        TODO("Not yet implemented")
    }

    fun irrigateArea(token: String, areaId: String) {
        TODO("Not yet implemented")
    }

    fun linkSensorToArea(token: String, areaId: String, sensorId: String): List<AreaRequest> {
        TODO("Not yet implemented")
    }

    fun unlinkSensorFromArea(token: String, sensorId: String): List<AreaRequest> {
        TODO("Not yet implemented")
    }

    fun getNotLinkedSensors(token: String): List<SensorResponse> {
        TODO("Not yet implemented")
    }
}