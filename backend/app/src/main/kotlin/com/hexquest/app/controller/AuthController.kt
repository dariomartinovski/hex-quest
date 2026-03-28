package com.hexquest.app.controller

import com.hexquest.app.dto.ApiResponse
import com.hexquest.app.dto.AuthResponse
import com.hexquest.app.dto.LoginRequest
import com.hexquest.app.dto.RegisterRequest
import com.hexquest.app.dto.UserProfileResponse
import com.hexquest.app.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val response = authService.register(request)
            ResponseEntity.ok(ApiResponse(data = response))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ApiResponse(errors = listOf(e.message ?: "Registration failed")))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val response = authService.login(request)
            ResponseEntity.ok(ApiResponse(data = response))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ApiResponse(errors = listOf("Invalid credentials")))
        }
    }

    @GetMapping("/me")
    fun getMe(authentication: Authentication?): ResponseEntity<ApiResponse<UserProfileResponse>> {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse(errors = listOf("Unauthorized")))
        }
        
        val username = authentication.name
        return try {
            val response = authService.getMe(username)
            ResponseEntity.ok(ApiResponse(data = response))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ApiResponse(errors = listOf(e.message ?: "Could not fetch profile")))
        }
    }
}
