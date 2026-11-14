package com.iotabuild.campuscircle.UserService.Models.Entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "match_preferences")
class MatchPreferenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    var code: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var title: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(columnDefinition = "TEXT")
    var icon: String? = null,

    @Column(nullable = false)
    var active: Boolean = true,

)
