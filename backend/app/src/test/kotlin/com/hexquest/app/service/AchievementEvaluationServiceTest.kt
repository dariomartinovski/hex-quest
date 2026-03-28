package com.hexquest.app.service

import com.hexquest.domain.*
import com.hexquest.infrastructure.repository.*
import com.hexquest.app.event.AchievementEvent
import com.hexquest.app.event.AchievementEventType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher

class AchievementEvaluationServiceTest {

    private lateinit var achievementRepository: AchievementRepository
    private lateinit var userAchievementRepository: UserAchievementRepository
    private lateinit var progressEntryRepository: ProgressEntryRepository
    private lateinit var eventPublisher: ApplicationEventPublisher

    private lateinit var service: AchievementEvaluationService

    @BeforeEach
    fun setup() {
        achievementRepository = mock()
        userAchievementRepository = mock()
        progressEntryRepository = mock()
        eventPublisher = mock()

        service = AchievementEvaluationService(
            achievementRepository,
            userAchievementRepository,
            progressEntryRepository,
            eventPublisher
        )
    }

    private fun createUser(id: Long) = User(
        id = id,
        username = "tester$id",
        email = "test$id@test.com",
        passwordHash = "hashed_pwd"
    )

    private fun createTask(id: Long) = Task(
        id = id,
        name = "Test Task",
        unitLabel = "wins"
    )

    private fun createAchievement(id: Long, typeName: String, threshold: Int? = null, minThreshold: Int? = null): Achievement {
        val type = AchievementType(name = typeName)
        return Achievement(
            id = id,
            task = createTask(1L),
            name = "Achv",
            description = null,
            badgeImageUrl = null,
            sex = null,
            category = null,
            type = type,
            thresholdValue = threshold,
            minThreshold = minThreshold
        )
    }

    @Test
    fun `threshold - first unlock when cumulative total meets threshold`() {
        val user = createUser(1L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "THRESHOLD", threshold = 10)

        whenever(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        whenever(userAchievementRepository.existsByUserIdAndAchievementId(1L, 10L)).thenReturn(false)

        service.evaluateTaskAchievements(user, task, 10)

        verify(userAchievementRepository, times(1)).save(any<UserAchievement>())
        verify(eventPublisher, times(1)).publishEvent(any<AchievementEvent>())
    }

    @Test
    fun `threshold - no unlock when user already has achievement`() {
        val user = createUser(1L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "THRESHOLD", threshold = 10)

        whenever(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        whenever(userAchievementRepository.existsByUserIdAndAchievementId(1L, 10L)).thenReturn(true)

        service.evaluateTaskAchievements(user, task, 15)

        verify(userAchievementRepository, never()).save(any<UserAchievement>())
    }

    @Test
    fun `supremacy - first unlock when no current holder`() {
        val user = createUser(1L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)

        whenever(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        whenever(userAchievementRepository.findByAchievementIdAndIsActiveTrue(10L)).thenReturn(null)

        service.evaluateTaskAchievements(user, task, 5)

        verify(userAchievementRepository, times(1)).save(any<UserAchievement>())
    }

    @Test
    fun `supremacy - transfers when new user strictly overtakes current holder`() {
        val oldUser = createUser(1L)
        val newUser = createUser(2L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)

        val activeRecord = UserAchievement(achievement = achievement, user = oldUser, isActive = true)

        whenever(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        whenever(userAchievementRepository.findByAchievementIdAndIsActiveTrue(10L)).thenReturn(activeRecord)

        val oldProgress = ProgressEntry(task = task, user = oldUser, delta = 1, cumulativeTotal = 10)
        whenever(progressEntryRepository.findTopByTaskIdAndUserIdOrderByIdDesc(1L, 1L)).thenReturn(oldProgress)

        service.evaluateTaskAchievements(newUser, task, 11)

        assertFalse(activeRecord.isActive)
        verify(userAchievementRepository, times(2)).save(any<UserAchievement>())
        verify(eventPublisher, times(2)).publishEvent(any<AchievementEvent>()) // REVOKED + STOLEN
    }

    @Test
    fun `supremacy - no transfer on tie, current holder retains`() {
        val oldUser = createUser(1L)
        val newUser = createUser(2L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)

        val activeRecord = UserAchievement(achievement = achievement, user = oldUser, isActive = true)

        whenever(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))
        whenever(userAchievementRepository.findByAchievementIdAndIsActiveTrue(10L)).thenReturn(activeRecord)

        val oldProgress = ProgressEntry(task = task, user = oldUser, delta = 1, cumulativeTotal = 10)
        whenever(progressEntryRepository.findTopByTaskIdAndUserIdOrderByIdDesc(1L, 1L)).thenReturn(oldProgress)

        service.evaluateTaskAchievements(newUser, task, 10)

        assertTrue(activeRecord.isActive)
        verify(userAchievementRepository, never()).save(any<UserAchievement>())
    }

    @Test
    fun `supremacy - ignored when cumulative total below minThreshold`() {
        val user = createUser(1L)
        val task = createTask(1L)
        val achievement = createAchievement(10L, "SUPREMACY", minThreshold = 5)

        whenever(achievementRepository.findByTaskId(1L)).thenReturn(listOf(achievement))

        service.evaluateTaskAchievements(user, task, 4)

        verify(userAchievementRepository, never()).findByAchievementIdAndIsActiveTrue(any())
        verify(userAchievementRepository, never()).save(any<UserAchievement>())
    }
}
