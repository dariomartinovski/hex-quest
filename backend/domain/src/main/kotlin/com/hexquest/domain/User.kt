package com.hexquest.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var username: String,

    @Column(name = "display_name")
    var displayName: String? = null,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column
    var sex: String? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
)
