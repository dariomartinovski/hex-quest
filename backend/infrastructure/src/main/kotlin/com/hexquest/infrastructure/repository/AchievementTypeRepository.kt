package com.hexquest.infrastructure.repository

import com.hexquest.domain.AchievementType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementTypeRepository : JpaRepository<AchievementType, Long> {
}
