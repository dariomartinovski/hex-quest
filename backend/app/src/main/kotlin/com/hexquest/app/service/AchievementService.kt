package com.hexquest.app.service

import com.hexquest.app.dto.*
import com.hexquest.domain.Achievement
import com.hexquest.infrastructure.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AchievementService(
    private val achievementRepository: AchievementRepository,
    private val taskRepository: TaskRepository,
    private val achievementCategoryRepository: AchievementCategoryRepository,
    private val achievementTypeRepository: AchievementTypeRepository,
    private val userAchievementRepository: UserAchievementRepository,
    private val userRepository: UserRepository
) {
    fun getAllCategories(): List<CategoryResponse> {
        return achievementCategoryRepository.findAll().map { cat ->
            CategoryResponse(cat.id!!, cat.name, cat.icon)
        }
    }

    fun getAllAchievements(): List<AchievementResponse> {
        return achievementRepository.findAll().map { toResponse(it) }
    }

    @Transactional
    fun createAchievement(request: CreateAchievementRequest): AchievementResponse {
        val task = taskRepository.findById(request.taskId)
            .orElseThrow { NoSuchElementException("Task not found") }
            
        val type = achievementTypeRepository.findByName(request.typeName.uppercase())
            .orElseThrow { NoSuchElementException("Achievement Type not found") }
            
        val category = request.categoryId?.let { 
            achievementCategoryRepository.findById(it)
                .orElseThrow { NoSuchElementException("Category not found") }
        }

        val achievement = Achievement(
            task = task,
            name = request.name,
            description = request.description,
            badgeImageUrl = request.badgeImageUrl,
            sex = request.sex,
            category = category,
            type = type,
            thresholdValue = request.thresholdValue,
            minThreshold = request.minThreshold
        )
        
        return toResponse(achievementRepository.save(achievement))
    }

    fun getUserAchievements(userId: Long): List<UserAchievementResponse> {
        if (!userRepository.existsById(userId)) {
            throw NoSuchElementException("User not found")
        }
        
        val userAchievements = userAchievementRepository.findByUserId(userId)
        
        return userAchievements.map { ua -> 
            UserAchievementResponse(
                id = ua.id!!,
                achievement = toResponse(ua.achievement),
                awardedAt = ua.awardedAt?.toString(),
                isActive = ua.isActive
            )
        }
    }
    
    private fun toResponse(achievement: Achievement): AchievementResponse {
        return AchievementResponse(
            id = achievement.id!!,
            taskId = achievement.task?.id ?: 0,
            name = achievement.name,
            description = achievement.description,
            badgeImageUrl = achievement.badgeImageUrl,
            sex = achievement.sex,
            categoryId = achievement.category?.id,
            typeName = achievement.type.name,
            thresholdValue = achievement.thresholdValue,
            minThreshold = achievement.minThreshold
        )
    }
}
