package com.hexquest.infrastructure.repository

import com.hexquest.domain.TaskParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskParticipantRepository : JpaRepository<TaskParticipant, Long> {
    fun existsByTaskIdAndUserId(taskId: Long, userId: Long): Boolean
    fun countByTaskId(taskId: Long): Long
    fun findByTaskId(taskId: Long): List<TaskParticipant>
}
