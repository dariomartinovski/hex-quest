package com.hexquest.app.dto

data class CreateTaskRequest(
    val name: String,
    val description: String? = null,
    val unitLabel: String
)

data class TaskResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val unitLabel: String,
    val creatorUsername: String
)

data class ParticipantProgressDto(
    val username: String,
    val cumulativeTotal: Int
)

data class TaskDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val unitLabel: String,
    val participantsCount: Long,
    val totalProgressEvents: Long
)

data class RecordProgressRequest(
    val delta: Int,
    val note: String? = null
)
