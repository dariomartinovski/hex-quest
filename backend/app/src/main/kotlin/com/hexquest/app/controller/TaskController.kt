package com.hexquest.app.controller

import com.hexquest.app.dto.ApiResponse
import com.hexquest.app.dto.CreateTaskRequest
import com.hexquest.app.dto.ParticipantProgressDto
import com.hexquest.app.dto.RecordProgressRequest
import com.hexquest.app.dto.TaskDetailResponse
import com.hexquest.app.dto.TaskResponse
import com.hexquest.app.service.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @GetMapping
    fun getAllTasks(): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        val tasks = taskService.getAllTasks()
        return ResponseEntity.ok(ApiResponse(data = tasks))
    }

    @PostMapping
    fun createTask(
        authentication: Authentication,
        @RequestBody request: CreateTaskRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {
        val username = authentication.name
        val task = taskService.createTask(username, request)
        return ResponseEntity.created(URI.create("/api/tasks/${task.id}")).body(ApiResponse(data = task))
    }

    @GetMapping("/{id}")
    fun getTaskDetails(@PathVariable id: Long): ResponseEntity<ApiResponse<TaskDetailResponse>> {
        val taskDetails = taskService.getTaskDetails(id)
        return ResponseEntity.ok(ApiResponse(data = taskDetails))
    }

    @PostMapping("/{id}/participants")
    fun joinTask(
        authentication: Authentication,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Nothing>> {
        val username = authentication.name
        taskService.joinTask(username, id)
        return ResponseEntity.ok(ApiResponse(meta = mapOf("message" to "Joined task successfully")))
    }

    @PostMapping("/{id}/progress")
    fun recordProgress(
        authentication: Authentication,
        @PathVariable id: Long,
        @RequestBody request: RecordProgressRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val username = authentication.name
        taskService.recordProgress(username, id, request)
        return ResponseEntity.ok(ApiResponse(meta = mapOf("message" to "Progress recorded")))
    }

    @GetMapping("/{id}/leaderboard")
    fun getLeaderboard(@PathVariable id: Long): ResponseEntity<ApiResponse<List<ParticipantProgressDto>>> {
        val leaderboard = taskService.getLeaderboard(id)
        return ResponseEntity.ok(ApiResponse(data = leaderboard))
    }
}
