package com.hexquest.app.service

import com.hexquest.domain.*
import com.hexquest.infrastructure.repository.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.springframework.context.ApplicationEventPublisher
import java.util.Optional

class AchievementEvaluationServiceTest {

    private lateinit var achievementRepository: AchievementRepository
    private lateinit var userAchievementRepository: UserAchievementRepository
    private lateinit var progressEntryRepository: ProgressEntryRepository
    private lateinit var eventPublisher: ApplicationEventPublisher

    private lateinit var service: AchievementEvaluationService

    @BeforeEach
    fun setup() {
        achievementRepository = mock(AchievementRepository::class.java)
        userAchievementRepository = mock(UserAchievementRepository::class.java)
        progressEntryRepository = mock(ProgressEntryRepository::class.java)
        eventPublisher = mock(ApplicationEventPublisher::class.java)

        service = AchievementEvaluationService(
            achievementRepository,
            userAchievementRepository,
            progressEntryRepository,
            eventPublisher
        )
    }

    private fun createUser(id: Long) = User(username = "tester$id", email = "test$id@test.com", password = "pwd").apply { this.id = id }
    private fun createTask(id: Long) = Task(name = "Test Task", unitLabel = "wins").apply { this.id = id }
    
    private fun createAchievement(id: Long, typeName: String, threshold: Int? = null, minThreshold: Int? = null): Achievement {
        val type = AchievementType(name = typeName)
        return Achievement(
            task = createTask(1L),
            name = "Achv",
            description = null,
            badgeImageUrl = null,
            sex = null,
            category = null,
            type = type,
            thresholdValue = threshold,
            minThreshold = minThreshold
        ).apply { this.id = id }
    }

    @Test
    fun `test Threshold First Unlock`() {
        val user = createUser(1L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "THRESHOLD", threshold = 10)

        `when`(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        `when`(userAchievementRepository.existsByUserIdAndAchievementId(1L, 10L)).thenReturn(false)

        service.evaluateTaskAchievements(user, task, 10)

        verify(userAchievementRepository, times(1)).save(any(UserAchievement::class.java))
    }

    @Test
    fun `test Supremacy First Unlock`() {
        val user = createUser(1L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)

        `when`(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        `when`(userAchievementRepository.findByAchievementIdAndIsActiveTrue(10L)).thenReturn(null)

        service.evaluateTaskAchievements(user, task, 5)

        verify(userAchievementRepository, times(1)).save(any(UserAchievement::class.java))
    }

    @Test
    fun `test Supremacy Transfer On Overtake`() {
        val oldUser = createUser(1L)
        val newUser = createUser(2L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)
        
        val activeRecord = UserAchievement(achievement = achievement, user = oldUser, isActive = true)

        `when`(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        `when`(userAchievementRepository.findByAchievementIdAndIsActiveTrue(10L)).thenReturn(activeRecord)
        
        // Mock latest progress of the OLD user
        val oldProgress = ProgressEntry(task = task, user = oldUser, delta = 1, cumulativeTotal = 10)
        `when`(progressEntryRepository.findTopByTaskIdAndUserIdOrderByIdDesc(1L, 1L)).thenReturn(oldProgress)

        service.evaluateTaskAchievements(newUser, task, 11) // New user has 11, strictly > 10

        assertFalse(activeRecord.isActive)
        // Two saves: one to deactivate old user, one to create new user record
        verify(userAchievementRepository, times(2)).save(any(UserAchievement::class.java))
    }

    @Test
    fun `test Supremacy No Transfer On Tie`() {
        val oldUser = createUser(1L)
        val newUser = createUser(2L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)
        
        val activeRecord = UserAchievement(achievement = achievement, user = oldUser, isActive = true)

        `when`(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        `when`(userAchievementRepository.findByAchievementIdAndIsActiveTrue(10L)).thenReturn(activeRecord)
        
        // Mock latest progress of the OLD user
        val oldProgress = ProgressEntry(task = task, user = oldUser, delta = 1, cumulativeTotal = 10)
        `when`(progressEntryRepository.findTopByTaskIdAndUserIdOrderByIdDesc(1L, 1L)).thenReturn(oldProgress)

        service.evaluateTaskAchievements(newUser, task, 10) // Tie case!

        assertTrue(activeRecord.isActive) // Should not have disabled the old record
        verify(userAchievementRepository, times(0)).save(any(UserAchievement::class.java))
    }

    @Test
    fun `test Supremacy Not Yet Met MinThreshold`() {
        val user = createUser(1L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)

        `when`(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))

        service.evaluateTaskAchievements(user, task, 4) // Only 4 total

        verify(userAchievementRepository, times(0)).findByAchievementIdAndIsActiveTrue(anyLong())
        verify(userAchievementRepository, times(0)).save(any(UserAchievement::class.java))
    }
}
