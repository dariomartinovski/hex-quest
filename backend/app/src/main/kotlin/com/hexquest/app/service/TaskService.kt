package com.hexquest.app.service

import com.hexquest.app.dto.*
import com.hexquest.domain.ProgressEntry
import com.hexquest.domain.Task
import com.hexquest.domain.TaskParticipant
import com.hexquest.infrastructure.repository.ProgressEntryRepository
import com.hexquest.infrastructure.repository.TaskParticipantRepository
import com.hexquest.infrastructure.repository.TaskRepository
import com.hexquest.infrastructure.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val taskParticipantRepository: TaskParticipantRepository,
    private val progressEntryRepository: ProgressEntryRepository,
    private val achievementEvaluationService: AchievementEvaluationService
) {
    fun getAllTasks(): List<TaskResponse> {
        return taskRepository.findAll().map { task ->
            TaskResponse(
                id = task.id!!,
                name = task.name,
                description = task.description,
                unitLabel = task.unitLabel,
                creatorUsername = task.createdBy?.username ?: "Unknown"
            )
        }
    }

    @Transactional
    fun createTask(username: String, request: CreateTaskRequest): TaskResponse {
        val user = userRepository.findByUsername(username).orElseThrow { NoSuchElementException("User not found") }
        
        val task = Task(
            name = request.name,
            description = request.description,
            unitLabel = request.unitLabel,
            createdBy = user
        )
        
        val savedTask = taskRepository.save(task)
        taskParticipantRepository.save(TaskParticipant(task = savedTask, user = user))

        return TaskResponse(
            id = savedTask.id!!,
            name = savedTask.name,
            description = savedTask.description,
            unitLabel = savedTask.unitLabel,
            creatorUsername = user.username
        )
    }

    fun getTaskDetails(taskId: Long): TaskDetailResponse {
        val task = taskRepository.findById(taskId).orElseThrow { NoSuchElementException("Task not found") }
        
        val progressEventsCount = progressEntryRepository.countByTaskId(taskId)
        val participantsCount = taskParticipantRepository.countByTaskId(taskId)

        return TaskDetailResponse(
            id = task.id!!,
            name = task.name,
            description = task.description,
            unitLabel = task.unitLabel,
            participantsCount = participantsCount,
            totalProgressEvents = progressEventsCount
        )
    }

    @Transactional
    fun joinTask(username: String, taskId: Long) {
        val user = userRepository.findByUsername(username).orElseThrow { NoSuchElementException("User not found") }
        val task = taskRepository.findById(taskId).orElseThrow { NoSuchElementException("Task not found") }
        
        val alreadyJoined = taskParticipantRepository.existsByTaskIdAndUserId(taskId, user.id!!)
        if (!alreadyJoined) {
            taskParticipantRepository.save(TaskParticipant(task = task, user = user))
        }
    }

    @Transactional
    fun recordProgress(username: String, taskId: Long, request: RecordProgressRequest) {
        require(request.delta > 0) { "Delta must be positive" }

        val user = userRepository.findByUsername(username).orElseThrow { NoSuchElementException("User not found") }
        val task = taskRepository.findById(taskId).orElseThrow { NoSuchElementException("Task not found") }

        val latestEntry = progressEntryRepository.findTopByTaskIdAndUserIdOrderByIdDesc(taskId, user.id!!)
        val latestCumulative = latestEntry?.cumulativeTotal ?: 0
        
        val newTotal = latestCumulative + request.delta
        
        val newEntry = ProgressEntry(
            task = task,
            user = user,
            delta = request.delta,
            cumulativeTotal = newTotal,
            note = request.note
        )
        progressEntryRepository.save(newEntry)
        
        achievementEvaluationService.evaluateTaskAchievements(user, task, newTotal)
    }

    fun getLeaderboard(taskId: Long): List<ParticipantProgressDto> {
        if (!taskRepository.existsById(taskId)) {
            throw NoSuchElementException("Task not found")
        }
        
        val participants = taskParticipantRepository.findByTaskId(taskId)
        
        val leaderboard = participants.map { participant ->
            val latestEntry = progressEntryRepository.findTopByTaskIdAndUserIdOrderByIdDesc(taskId, participant.user.id!!)
            val total = latestEntry?.cumulativeTotal ?: 0
            ParticipantProgressDto(participant.user.username, total)
        }.sortedByDescending { it.cumulativeTotal }

        return leaderboard
    }
}
