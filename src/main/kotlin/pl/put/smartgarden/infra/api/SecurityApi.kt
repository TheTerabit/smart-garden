package pl.put.smartgarden.infra.api

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import pl.put.smartgarden.domain.user.UserService
import pl.put.smartgarden.domain.user.dto.UserSignInDto
import pl.put.smartgarden.domain.user.dto.UserSignUpDto
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class SecurityApi(val userService: UserService) {

    @PostMapping("/sign-up")
    fun signUp(@Valid @RequestBody user: UserSignUpDto) {
        userService.signUpUser(user)
    }

    @GetMapping("/sign-in")
    fun signIn(@RequestBody user: UserSignInDto) {
    }
}