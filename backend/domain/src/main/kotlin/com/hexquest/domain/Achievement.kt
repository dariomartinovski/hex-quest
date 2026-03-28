package com.hexquest.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "achievements")
class Achievement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    var task: Task? = null,

    @Column(nullable = false)
    var name: String,

    @Column
    var description: String? = null,

    @Column(name = "badge_image_url")
    var badgeImageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column
    var sex: SexType? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: AchievementCategory? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    var type: AchievementType,

    @Column(name = "threshold_value")
    var thresholdValue: Int? = null,

    @Column(name = "min_threshold")
    var minThreshold: Int? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
)
