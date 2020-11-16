package pl.put.smartgarden.domain.user

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.dto.response.AreaResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.exception.UserAlreadyExistsException
import pl.put.smartgarden.domain.user.repository.UserRepository
import java.time.Instant

@Service
class UserService(
    val securityService: SecurityService,
    val deviceService: UserDeviceService,
    val userRepository: UserRepository
) {

    fun signUpUser(userDto: UserSignUpRequest) =
        if (isUserUnique(userDto)) {
            var user = securityService.createUser(userDto)
            user = userRepository.save(user)

            deviceService.createDevice(userDto.deviceGuid, userDto.latitude, userDto.longitude, user.id)
            securityService.sendVerificationEmail(userDto, user)
        } else {
            throw UserAlreadyExistsException("User with this name or email already exists.", HttpStatus.CONFLICT)
        }

    private fun isUserUnique(userRequest: UserSignUpRequest): Boolean =
        userRepository.findByEmail(userRequest.email) == null && userRepository.findByUsername(userRequest.username) == null

    fun signIn(userSignInRequest: UserSignInRequest): UserSignInResponse {
        val user = userRepository.findByEmail(userSignInRequest.email)

        user ?: throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
        if (!user.enabled) throw SmartGardenException("Account is not enabled.", HttpStatus.UNAUTHORIZED)
        securityService.validateUserPassword(userSignInRequest.password, user.password)

        return UserSignInResponse(
            token = securityService.generateJsonWebTokenFromUser(user),
            username = user.username,
            id = user.id
        )
    }

    fun enableUserIfValid(token: String) {
        val user = securityService.getUserFromVerificationToken(token)
        user.enabled = true
        userRepository.save(user)
    }

    fun getUserGeneralSettings(token: String): UserGeneralSettingsResponse {
        val user = securityService.getUserFromJWToken(token)
        return UserGeneralSettingsResponse(
            username = user.username,
            email = user.email,
            deviceGuid = user.device?.guid,
            latitude = user.device?.latitude,
            longitude = user.device?.longitude
        )
    }

    fun signOut(token: String) {
        securityService.revokeToken(token)
    }
}