package com.hexquest.infrastructure.repository

import com.hexquest.domain.TaskParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskParticipantRepository : JpaRepository<TaskParticipant, Long> {
}
