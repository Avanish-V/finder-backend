package com.iotabuild.campuscircle.FeedService.Models.Entity

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "post_likes",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_post_like_user",
            columnNames = ["post_id", "user_id"]
        )
    ],
    indexes = [
        Index(
            name = "idx_post_likes_post_id",
            columnList = "post_id"
        ),
        Index(
            name = "idx_post_likes_user_id",
            columnList = "user_id"
        )
    ]
)
class PostLikeEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "post_id",
        nullable = false
    )
    var post: PostsEntity,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    var user: UserEntity,

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    var createdAt: Instant = Instant.now()
)