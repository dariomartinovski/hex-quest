package com.hexquest.app.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val displayName: String? = null,
    val sex: String? = null
)

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserProfileResponse
)

data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val sex: String?
)

// Generic API Response Wrapper
data class ApiResponse<T>(
    val data: T? = null,
    val meta: Map<String, Any>? = null,
    val errors: List<String>? = null
)
