package pl.put.smartgarden.domain.user

import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

//@Service
//class UserDetailsServiceImpl : UserDetailsService {
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @Transactional(readOnly = true)
//    override fun loadUserByUsername(email: String): UserDetails {
//        val user: User = userRepository.findByEmail(email)
//                ?: throw UsernameNotFoundException(email)
//
//        return org.springframework.security.core.userdetails.User(user.email,
//                user.password, user.enabled, true,
//                true, true,
//                getAuthorities("USER"))
//    }
//
//    private fun getAuthorities(role: String): Collection<GrantedAuthority>? {
//        val authorities: MutableList<GrantedAuthority> = ArrayList()
//        authorities.add(SimpleGrantedAuthority(role))
//        return authorities
//    }
//}