package com.hexquest.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "task_participants")
class TaskParticipant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: Task,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "joined_at", insertable = false, updatable = false)
    val joinedAt: OffsetDateTime? = null
)
