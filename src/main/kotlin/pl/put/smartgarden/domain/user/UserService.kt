package pl.put.smartgarden.domain.user

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.user.dto.UserResourceDto
import pl.put.smartgarden.domain.user.dto.UserSignUpDto
import java.util.stream.Collectors

@Service
class UserService(
        val userRepository: UserRepository,
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
        val user = User()
        user.username = userDto.username
        user.email = userDto.email
        user.enabled = false
        user.password = bCryptPasswordEncoder.encode(userDto.password)
    }
}