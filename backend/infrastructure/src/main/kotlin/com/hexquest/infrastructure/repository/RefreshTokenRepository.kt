package com.hexquest.infrastructure.repository

import com.hexquest.domain.RefreshToken
import com.hexquest.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByTokenHash(tokenHash: String): Optional<RefreshToken>
    fun deleteByUser(user: User)
}
