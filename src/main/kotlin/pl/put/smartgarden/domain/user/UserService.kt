package pl.put.smartgarden.domain.user

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.exception.UserAlreadyExistsException
import pl.put.smartgarden.domain.user.repository.UserRepository

@Service
class UserService(
    private val authService: UserAuthService,
    private val deviceService: UserDeviceService,
    private val userRepository: UserRepository
) {

    fun signUpUser(userDto: UserSignUpRequest) =
        if (isUserUnique(userDto)) {
            var user = authService.createUser(userDto)
            user = userRepository.save(user)

            val device = deviceService.createAndSaveDevice(userDto.deviceGuid, userDto.latitude, userDto.longitude, user.id)
            user.device = device
            userRepository.saveAndFlush(user)

            authService.sendVerificationEmail(userDto, user)
        } else {
            throw UserAlreadyExistsException("User with this name or email already exists.", HttpStatus.CONFLICT)
        }

    private fun isUserUnique(userRequest: UserSignUpRequest): Boolean =
        userRepository.findByEmail(userRequest.email) == null && userRepository.findByUsername(userRequest.username) == null

    fun signIn(userSignInRequest: UserSignInRequest): UserSignInResponse {
        val user = userRepository.findByEmail(userSignInRequest.email)

        user ?: throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)
        if (!user.enabled) throw SmartGardenException("Account is not enabled.", HttpStatus.UNAUTHORIZED)
        if (!authService.isUserPasswordCorrect(userSignInRequest.password, user.password))
            throw SmartGardenException("Bad login or password.", HttpStatus.BAD_REQUEST)

        return UserSignInResponse(
            token = authService.generateJsonWebTokenFromUser(user),
            username = user.username
        )
    }

    fun enableUserIfValid(token: String) {
        val user = authService.getUserFromVerificationToken(token)
        user.enabled = true
        userRepository.save(user)
    }

    fun getUserGeneralSettings(userId: Int): UserGeneralSettingsResponse {
        val user = userRepository.getUserById(userId)

        return UserGeneralSettingsResponse(
            username = user.username,
            email = user.email,
            deviceGuid = user.device?.guid,
            latitude = user.device?.latitude,
            longitude = user.device?.longitude
        )
    }

    fun signOut(token: String) {
        authService.revokeToken(token);
    }
}