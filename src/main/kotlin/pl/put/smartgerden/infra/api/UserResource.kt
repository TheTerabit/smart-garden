package pl.put.smartgerden.infra.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgerden.domain.user.User
import pl.put.smartgerden.domain.user.UserService

@RestController
@RequestMapping("/users")
class UserResource(
    val userService: UserService
) {
    @PostMapping
    fun createUser(@RequestBody user: User) {
        userService.createUser(user)
    }

    @GetMapping
    fun getUsers(): List<User> = userService.getUsers()
}