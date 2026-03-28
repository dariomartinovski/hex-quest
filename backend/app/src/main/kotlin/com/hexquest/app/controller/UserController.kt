package com.hexquest.app.controller

import com.hexquest.app.dto.ApiResponse
import com.hexquest.app.dto.UserAchievementResponse
import com.hexquest.app.service.AchievementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val achievementService: AchievementService) {

    @GetMapping("/{id}/achievements")
    fun getUserAchievements(@PathVariable id: Long): ResponseEntity<ApiResponse<List<UserAchievementResponse>>> {
        val achievements = achievementService.getUserAchievements(id)
        return ResponseEntity.ok(ApiResponse(data = achievements))
    }
}
