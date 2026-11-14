package com.iotabuild.campuscircle.UserService.Models.Entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "user_match_preferences",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_user_match_preference",
            columnNames = ["user_id", "preference_id"]
        )
    ]
)
class UserMatchPreferenceEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "preference_id",
        nullable = false
    )
    var preference: MatchPreferenceEntity,

)
