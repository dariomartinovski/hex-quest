package com.hexquest.domain

import jakarta.persistence.*

@Entity
@Table(name = "achievement_types")
class AchievementType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var name: String,
    
    @Column
    var description: String? = null
)
