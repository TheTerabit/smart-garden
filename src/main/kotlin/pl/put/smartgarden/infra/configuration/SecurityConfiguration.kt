package pl.put.smartgarden.infra.configuration

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.put.smartgarden.domain.SmartGardenException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


@Configuration
@EnableWebSecurity
@EnableEncryptableProperties
class SecurityConfigurationn : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/*")
            .permitAll()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterRegistrationBean(filter: JwtFilter): FilterRegistrationBean<Filter> {
        val filterRegistrationBean = FilterRegistrationBean<Filter>()
        filterRegistrationBean.filter = filter
        filterRegistrationBean.urlPatterns = listOf("/users/me", "/users/me/*")
        return filterRegistrationBean
    }

    @Bean
    fun filter(): JwtFilter = JwtFilter()
}

class JwtFilter : Filter {

    @Value("\${jwt-secret-key}")
    lateinit var secretKey: String

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val httpServletRequest = servletRequest as HttpServletRequest
        val header = httpServletRequest.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            throw SmartGardenException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED)
        } else {
            setServletRequestClaims(header, servletRequest)
        }

        filterChain.doFilter(servletRequest, servletResponse)
    }

    private fun setServletRequestClaims(header: String, servletRequest: ServletRequest) {
        try {
            val token = header.substring(7)
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
            servletRequest.setAttribute("claims", claims)
        } catch (e: SignatureException) {
            throw SmartGardenException("Invalid token", HttpStatus.UNAUTHORIZED)
        }
    }
}