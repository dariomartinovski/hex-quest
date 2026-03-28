package com.hexquest.infrastructure.repository

import com.hexquest.domain.AchievementCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementCategoryRepository : JpaRepository<AchievementCategory, Long> {
}
