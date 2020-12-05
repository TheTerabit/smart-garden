package pl.put.smartgarden.domain.user

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.user.dto.request.UserChangeEmailRequest
import pl.put.smartgarden.domain.user.dto.request.UserChangePasswordRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInRequest
import pl.put.smartgarden.domain.user.dto.request.UserSignInResponse
import pl.put.smartgarden.domain.user.dto.request.UserSignUpRequest
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.exception.UserAlreadyExistsException
import pl.put.smartgarden.domain.user.repository.UserRepository

@Service
@Transactional
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
        val user = getUserById(userId)

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

    private fun getUserById(id: Int): User {
        val userOptional = userRepository.findById(id)

        if (!userOptional.isPresent) throw SmartGardenException("Invalid token", HttpStatus.UNAUTHORIZED)

        return userOptional.get()
    }

    fun changePassword(userId: Int, request: UserChangePasswordRequest): UserGeneralSettingsResponse {
        val user = getUserById(userId)
        if (authService.isUserPasswordCorrect(request.oldPassword, user.password)) {
            if (request.password == request.passwordRepeated) {
                val userWithUpdatedPassword = authService.updateUserPassword(user, request.password)
                val savedUser = userRepository.saveAndFlush(userWithUpdatedPassword)
                return UserGeneralSettingsResponse(savedUser.username, savedUser.email, savedUser.device?.guid, savedUser.device?.latitude, savedUser.device?.longitude)
            } else {
                throw SmartGardenException("Passwords don't match.", HttpStatus.BAD_REQUEST)
            }
        } else {
            throw SmartGardenException("Wrong password.", HttpStatus.BAD_REQUEST)
        }
    }

    fun changeEmail(userId: Int, userRequest: UserChangeEmailRequest): UserGeneralSettingsResponse {
        TODO("Not yet implemented")
    }
}