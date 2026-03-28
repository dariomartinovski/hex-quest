package com.hexquest.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "progress_entries")
class ProgressEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: Task,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false)
    var delta: Int,

    @Column(name = "cumulative_total", nullable = false)
    var cumulativeTotal: Int,

    @Column
    var note: String? = null,

    @Column(name = "recorded_at", insertable = false, updatable = false)
    val recordedAt: OffsetDateTime? = null
)
