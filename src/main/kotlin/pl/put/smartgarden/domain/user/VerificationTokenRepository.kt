package pl.put.smartgarden.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VerificationTokenRepository : JpaRepository<VerificationToken, String> {
    fun findByToken(token : String) : VerificationToken?
}