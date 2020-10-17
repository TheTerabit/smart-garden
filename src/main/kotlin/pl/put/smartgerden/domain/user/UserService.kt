package pl.put.smartgerden.domain.user

import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository
) {
    fun getUsers(): List<User> = userRepository.findAll()
    fun createUser(user: User) {
        userRepository.save(user)
    }
}