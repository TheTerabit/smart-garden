package pl.put.smartgarden.infra.configuration

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.put.smartgarden.domain.SecurityService
import pl.put.smartgarden.domain.SmartGardenException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


@Configuration
@EnableWebSecurity
@EnableEncryptableProperties
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**").permitAll()
            .antMatchers("/users", "/users/login", "/users/logout", "/users/sign-up-confirmation").permitAll()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userFilterRegistrationBean(filter: JwtFilter): FilterRegistrationBean<Filter> {
        val filterRegistrationBean = FilterRegistrationBean<Filter>()
        filterRegistrationBean.filter = filter
        filterRegistrationBean.addUrlPatterns("/users/me", "/users/me/**", "/devices/**")
        filterRegistrationBean.order = 1
        return filterRegistrationBean
    }

    @Bean
    fun filter(): JwtFilter = JwtFilter()
}

class JwtFilter() : Filter {
    @Autowired lateinit var securityService: SecurityService

    @Value("\${jwt-secret-key}")
    lateinit var secretKey: String

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val httpServletRequest = servletRequest as HttpServletRequest
        val header = httpServletRequest.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            throw SmartGardenException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED)
        } else {
            try {
                val token = header.substring(7)
                if (securityService.isTokenRevoked(token)) {
                    throw SmartGardenException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED)
                }

                val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

                servletRequest.setAttribute("id", claims["sub"])
                servletRequest.setAttribute("role", claims["role"])
                servletRequest.setAttribute("token", token)
            } catch (e: SignatureException) {
                throw SmartGardenException("Invalid token", HttpStatus.UNAUTHORIZED)
            }
        }

        filterChain.doFilter(servletRequest, servletResponse)
    }
}
