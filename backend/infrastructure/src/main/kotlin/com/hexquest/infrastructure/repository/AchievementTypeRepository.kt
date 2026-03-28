package com.hexquest.infrastructure.repository

import com.hexquest.domain.AchievementType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional

@Repository
interface AchievementTypeRepository : JpaRepository<AchievementType, Long> {
    fun findByName(name: String): Optional<AchievementType>
}
