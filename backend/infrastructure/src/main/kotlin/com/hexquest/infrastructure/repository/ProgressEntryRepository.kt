package com.hexquest.infrastructure.repository

import com.hexquest.domain.ProgressEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgressEntryRepository : JpaRepository<ProgressEntry, Long> {
    fun countByTaskId(taskId: Long): Long
    fun findTopByTaskIdAndUserIdOrderByIdDesc(taskId: Long, userId: Long): ProgressEntry?
    fun findTopByTaskIdAndUserIdOrderByRecordedAtDesc(taskId: Long, userId: Long): ProgressEntry?
}
