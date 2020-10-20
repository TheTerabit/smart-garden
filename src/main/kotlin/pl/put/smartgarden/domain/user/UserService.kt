package pl.put.smartgarden.domain.user

import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.user.dto.UserResourceDto
import pl.put.smartgarden.domain.user.dto.UserSignUpDto
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

    fun getUsers(): List<UserResourceDto> = userRepository.findAll()
            .stream()
            .map { user ->
                UserResourceDto(
                        username = user.username.orEmpty(),
                        email = user.email.orEmpty(),
                        deviceGuid = user.device?.id.orEmpty()
                )
            }
            .collect(Collectors.toList())

    fun signUpUser(userDto: UserSignUpDto) {
        if (isUserUnique(userDto)) {
            val user = User()
            user.username = userDto.username
            user.email = userDto.email
            user.enabled = false
            user.password = bCryptPasswordEncoder.encode(userDto.password)

            userRepository.save(user)

            val token = mailService.sendVerificationEmail(userDto.email)

            val verificationToken = VerificationToken(token, user)

            verificationTokenRepository.save(verificationToken)

            return
        }

        throw UserAlreadyExistsException("User with this name or email already exists.", HttpStatus.CONFLICT)
    }

    private fun isUserUnique(userDto: UserSignUpDto): Boolean {
        return userRepository.findByEmail(userDto.email) == null && userRepository.findByUsername(userDto.username) == null
    }
}