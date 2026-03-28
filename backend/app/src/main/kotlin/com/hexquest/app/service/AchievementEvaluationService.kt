package com.hexquest.app.service

import com.hexquest.app.event.AchievementEvent
import com.hexquest.domain.Task
import com.hexquest.domain.User
import com.hexquest.domain.UserAchievement
import com.hexquest.infrastructure.repository.AchievementRepository
import com.hexquest.infrastructure.repository.ProgressEntryRepository
import com.hexquest.infrastructure.repository.UserAchievementRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AchievementEvaluationService(
    private val achievementRepository: AchievementRepository,
    private val userAchievementRepository: UserAchievementRepository,
    private val progressEntryRepository: ProgressEntryRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    @Transactional
    fun evaluateTaskAchievements(user: User, task: Task, newCumulativeTotal: Int) {
        val achievements = achievementRepository.findByTaskId(task.id!!)

        for (achievement in achievements) {
            val typeName = achievement.type.name

            if (typeName == "THRESHOLD") {
                val threshold = achievement.thresholdValue ?: continue
                if (newCumulativeTotal >= threshold) {
                    val alreadyHas = userAchievementRepository.existsByUserIdAndAchievementId(user.id!!, achievement.id!!)
                    if (!alreadyHas) {
                        val ua = UserAchievement(
                            achievement = achievement,
                            user = user,
                            isActive = true
                        )
                        userAchievementRepository.save(ua)
                        eventPublisher.publishEvent(AchievementEvent(user.id!!, achievement.id!!, com.hexquest.app.event.AchievementEventType.UNLOCKED))
                    }
                }
            } else if (typeName == "SUPREMACY") {
                val minThreshold = achievement.minThreshold ?: continue
                
                if (newCumulativeTotal >= minThreshold) {
                    val currentHolderRecord = userAchievementRepository.findByAchievementIdAndIsActiveTrue(achievement.id!!)
                    
                    if (currentHolderRecord == null) {
                        val ua = UserAchievement(
                            achievement = achievement,
                            user = user,
                            isActive = true
                        )
                        userAchievementRepository.save(ua)
                        eventPublisher.publishEvent(AchievementEvent(user.id!!, achievement.id!!, com.hexquest.app.event.AchievementEventType.UNLOCKED))
                    } else if (currentHolderRecord.user.id != user.id) {
                        val holderLatestEntry = progressEntryRepository.findTopByTaskIdAndUserIdOrderByIdDesc(task.id!!, currentHolderRecord.user.id!!)
                        val holderTotal = holderLatestEntry?.cumulativeTotal ?: 0
                        
                        // Strict inequality required for transferring supremacy (ties stay with previous holder)
                        if (newCumulativeTotal > holderTotal) {
                            currentHolderRecord.isActive = false
                            userAchievementRepository.save(currentHolderRecord)
                            
                            val ua = UserAchievement(
                                achievement = achievement,
                                user = user,
                                isActive = true
                            )
                            userAchievementRepository.save(ua)
                            eventPublisher.publishEvent(AchievementEvent(currentHolderRecord.user.id!!, achievement.id!!, com.hexquest.app.event.AchievementEventType.REVOKED))
                            eventPublisher.publishEvent(AchievementEvent(user.id!!, achievement.id!!, com.hexquest.app.event.AchievementEventType.STOLEN))
                        }
                    }
                }
            }
        }
    }
}
