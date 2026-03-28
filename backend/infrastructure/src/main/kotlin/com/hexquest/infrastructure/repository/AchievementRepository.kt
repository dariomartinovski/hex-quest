package com.hexquest.infrastructure.repository

import com.hexquest.domain.Achievement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementRepository : JpaRepository<Achievement, Long> {
}
