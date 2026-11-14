package com.iotabuild.campuscircle.FeedService.Models.Entity

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID


enum class PostType {
    TEXT,
    IMAGE,
    VIDEO,
    DOCUMENT,
    FILE,
    LINK
}
enum class MediaType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    FILE
}


@Entity
@Table(name = "feed_post")
class PostsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: UserEntity,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    var mediaList: MutableList<FeedMediaEntity> = mutableListOf(),

    @Column(nullable = false, columnDefinition = "TEXT")
    var caption: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var postType: PostType = PostType.TEXT,

    @Column(nullable = false)
    var likeCount: Long = 0,

    @Column( nullable = false)
    var replyCount: Long = 0,

    @Column(nullable = false)
    var deleted: Boolean = false
)
