package com.hexquest.infrastructure.repository

import com.hexquest.domain.ProgressEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgressEntryRepository : JpaRepository<ProgressEntry, Long> {
}
