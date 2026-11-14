package com.iotabuild.campuscircle.FeedService.Models.Entity

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "replies",
    indexes = [
        Index(name = "idx_replies_post_id", columnList = "post_id"),
        Index(name = "idx_replies_parent_id", columnList = "parent_id"),
        Index(name = "idx_replies_author_id", columnList = "author_id"),
        Index(name = "idx_replies_created_at", columnList = "created_at")
    ]
)
class ReplyEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    var id: UUID? = null,

    /*
     * Every reply belongs to the original post.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    var post: PostsEntity,

    /*
     * Who created this reply.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    var author: UserEntity,

    /*
     * Parent reply.
     *
     * NULL  → direct reply to post
     * UUID  → reply to another reply
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: ReplyEntity? = null,

    @Column(length = 2000)
    var content: String = "",

    /*
     * One optional image.
     */
    @Column(name = "image_url")
    var imageUrl: String? = null,

    /*
     * Optional user being mentioned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_user_id")
    var mentionedUser: UserEntity? = null,

    @Column(nullable = false)
    var deleted: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @Column(nullable = false)
    val replyCount: Int = 0,

    @Column(nullable = false)
    var likesCount: Int = 0



)