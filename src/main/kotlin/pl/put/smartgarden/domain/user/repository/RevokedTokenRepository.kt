package pl.put.smartgarden.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import pl.put.smartgarden.domain.user.RevokedToken

interface RevokedTokenRepository : JpaRepository<RevokedToken, String>{
}