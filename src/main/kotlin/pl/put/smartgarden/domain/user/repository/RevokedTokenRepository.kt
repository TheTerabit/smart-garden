package pl.put.smartgarden.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.put.smartgarden.domain.user.RevokedToken
@Repository
interface RevokedTokenRepository : JpaRepository<RevokedToken, String>{
}