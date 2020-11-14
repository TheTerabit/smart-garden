package pl.put.smartgarden.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.user.VerificationToken

@Repository
interface VerificationTokenRepository : JpaRepository<VerificationToken, Int> {
    fun findByToken(token: String): VerificationToken?
}