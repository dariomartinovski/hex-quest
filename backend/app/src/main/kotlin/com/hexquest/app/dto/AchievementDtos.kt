package com.hexquest.app.dto

import com.hexquest.domain.SexType

data class CreateAchievementRequest(
    val taskId: Long,
    val name: String,
    val description: String? = null,
    val badgeImageUrl: String? = null,
    val sex: SexType? = null,
    val categoryId: Long? = null,
    val typeName: String, // e.g. "THRESHOLD" or "SUPREMACY"
    val thresholdValue: Int? = null,
    val minThreshold: Int? = null
)

data class AchievementResponse(
    val id: Long,
    val taskId: Long,
    val name: String,
    val description: String?,
    val badgeImageUrl: String?,
    val sex: SexType?,
    val categoryId: Long?,
    val typeName: String,
    val thresholdValue: Int?,
    val minThreshold: Int?
)

data class UserAchievementResponse(
    val id: Long,
    val achievement: AchievementResponse,
    val awardedAt: String?,
    val isActive: Boolean
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val icon: String?
)
