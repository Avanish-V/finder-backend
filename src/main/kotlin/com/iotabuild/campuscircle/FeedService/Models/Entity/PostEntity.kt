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
import java.time.LocalDateTime


enum class Visibility { ANONYMOUS, USER }
enum class PostType {TEXT,MEDIA, POLL}
enum class MediaType { IMAGE }


@Entity
@Table(name = "posts")
class PostsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val postId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: UserEntity,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),


    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val mediaList: MutableList<MediaPostEntity> = mutableListOf(),




    @Column(name = "caption", columnDefinition = "TEXT")
    var caption: String?=null,

    @Enumerated(EnumType.STRING)
    val postType: PostType,

    @Column(name = "like_count", nullable = true)
    var likeCount: Long? = 0,

    @Column(name = "reply_count", nullable = true)
    var replyCount: Long? = 0,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val likes: MutableList<PostLikeEntity> = mutableListOf(),


) {
    // 👇 Hibernate needs this
    protected constructor() : this(
        postId = 0,
        author = UserEntity(),   // ⚠ you'll need a no-arg constructor in UserEntity too
        createdAt = LocalDateTime.now(),
        caption = null,
        postType = PostType.MEDIA, // provide a safe default
        likeCount = 0
    )
}



@Entity
@Table(name = "text_posts")
class TextPostEntity(

    @Id
    val postId: Long = 0,  // same ID as parent post

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    val post: PostsEntity? = null,

    @Column(columnDefinition = "TEXT")
    var text: String? = ""
)


@Entity
@Table(name = "media_posts")
class MediaPostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: PostsEntity? = null,

    @Column(nullable = false)
    val mediaUrl: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val mediaType: MediaType = MediaType.IMAGE
)


@Entity
@Table(name = "poll_posts")
class PollPostEntity(
    @Id
    val postId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    val post: PostsEntity? = null,

    val question: String = "",

    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true)
    val options: MutableList<PollOptionEntity> = mutableListOf()
)

@Entity
@Table(name = "poll_options_v2")
class PollOptionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_post_id")
    val poll: PollPostEntity? = null,

    val optionText: String = "",

    @OneToMany(mappedBy = "option", cascade = [CascadeType.ALL], orphanRemoval = true)
    val votes: MutableList<PollVoteEntity> = mutableListOf()
)

@Entity
@Table(name = "poll_votes_v2", uniqueConstraints = [
    UniqueConstraint(columnNames = ["user_id", "poll_post_id"])
])
class PollVoteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_post_id")
    val poll: PollPostEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    val option: PollOptionEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity? = null
)


