package com.hexquest.app.event

enum class AchievementEventType {
    UNLOCKED,
    REVOKED,
    STOLEN
}

data class AchievementEvent(
    val userId: Long,
    val achievementId: Long,
    val eventType: AchievementEventType
)
