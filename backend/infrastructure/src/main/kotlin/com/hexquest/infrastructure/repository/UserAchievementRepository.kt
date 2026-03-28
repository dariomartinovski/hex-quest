package com.hexquest.infrastructure.repository

import com.hexquest.domain.UserAchievement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAchievementRepository : JpaRepository<UserAchievement, Long> {
    fun findByUserId(userId: Long): List<UserAchievement>
}
