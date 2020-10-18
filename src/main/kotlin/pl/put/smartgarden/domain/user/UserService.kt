package pl.put.smartgarden.domain.user

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.user.dto.UserSignUpDto

@Service
class UserService(
    val userRepository: UserRepository,
    val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    fun getUsers(): List<User> = userRepository.findAll()
    fun signUpUser(userDto: UserSignUpDto) {
        val user = User()
        user.username = userDto.username
        user.email = userDto.email
        user.enabled = false
        user.password = bCryptPasswordEncoder.encode(userDto.password)

    }
}