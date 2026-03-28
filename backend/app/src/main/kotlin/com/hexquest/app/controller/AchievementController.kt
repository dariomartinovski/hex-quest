package com.hexquest.app.controller

import com.hexquest.app.dto.*
import com.hexquest.app.service.AchievementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/achievements")
class AchievementController(private val achievementService: AchievementService) {

    @GetMapping
    fun getAllAchievements(): ResponseEntity<ApiResponse<List<AchievementResponse>>> {
        val achievements = achievementService.getAllAchievements()
        return ResponseEntity.ok(ApiResponse(data = achievements))
    }

    @PostMapping
    fun createAchievement(@RequestBody request: CreateAchievementRequest): ResponseEntity<ApiResponse<AchievementResponse>> {
        val achievement = achievementService.createAchievement(request)
        return ResponseEntity.created(URI.create("/api/achievements/${achievement.id}"))
            .body(ApiResponse(data = achievement))
    }

    @GetMapping("/categories")
    fun getCategories(): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        val categories = achievementService.getAllCategories()
        return ResponseEntity.ok(ApiResponse(data = categories))
    }
}
