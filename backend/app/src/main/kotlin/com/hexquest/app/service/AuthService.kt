package com.hexquest.app.service

import com.hexquest.app.dto.AuthResponse
import com.hexquest.app.dto.LoginRequest
import com.hexquest.app.dto.RegisterRequest
import com.hexquest.app.dto.UserProfileResponse
import com.hexquest.domain.RefreshToken
import com.hexquest.domain.User
import com.hexquest.infrastructure.repository.RefreshTokenRepository
import com.hexquest.infrastructure.repository.UserRepository
import com.hexquest.infrastructure.security.JwtService
import com.hexquest.infrastructure.security.SecurityUser
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            username = request.username,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            displayName = request.displayName,
            sex = request.sex
        )

        val savedUser = userRepository.save(user)
        return generateAuthResponse(savedUser)
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        // Find by username or email
        val user = userRepository.findByUsername(request.usernameOrEmail)
            .orElseGet { 
                userRepository.findByEmail(request.usernameOrEmail)
                    .orElseThrow { IllegalArgumentException("Invalid credentials") } 
            }

        // Authenticate via Spring Security manager to check password
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(user.username, request.password)
        )

        // Generate tokens
        return generateAuthResponse(user)
    }

    private fun generateAuthResponse(user: User): AuthResponse {
        val securityUser = SecurityUser(user)
        val accessToken = jwtService.generateToken(securityUser)
        
        // Generate a random refresh token string and hash it for DB storage
        // A simple approach is using a UUID
        val refreshTokenString = UUID.randomUUID().toString()
        
        // Save refresh token to DB. Hash it in a real-world scenario; for simplicity we just store the UUID.
        // We'll give it 7 days validity
        val rt = RefreshToken(
            user = user,
            tokenHash = refreshTokenString, // Just storing the raw UUID token here for simplicity
            expiresAt = OffsetDateTime.now().plusDays(7)
        )
        refreshTokenRepository.save(rt)

        val userProfile = UserProfileResponse(
            id = user.id!!,
            username = user.username,
            email = user.email,
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            sex = user.sex
        )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshTokenString,
            user = userProfile
        )
    }

    @Transactional(readOnly = true)
    fun getMe(username: String): UserProfileResponse {
        val user = userRepository.findByUsername(username)
            .orElseThrow { IllegalArgumentException("User not found") }

        return UserProfileResponse(
            id = user.id!!,
            username = user.username,
            email = user.email,
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            sex = user.sex
        )
    }
}
