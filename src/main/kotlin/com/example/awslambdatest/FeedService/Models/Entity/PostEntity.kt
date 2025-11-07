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


enum class FeedMode { CAMPUS,OPEN }
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

    @Enumerated(EnumType.STRING)
    val feedMode: FeedMode = FeedMode.OPEN,


    val campusId: String? = null,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val mediaList: MutableList<MediaPostEntity> = mutableListOf(),

    @Column(name = "content")
    var content: String?=null,

    @Enumerated(EnumType.STRING)
    val visibility: Visibility = Visibility.USER,

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
        feedMode = FeedMode.OPEN,
        campusId = null,
        content = null,
        visibility = Visibility.USER,
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
    val post: PostsEntity,

    @Column(columnDefinition = "TEXT")
    var text: String? = ""
)


@Entity
@Table(name = "media_posts")
data class MediaPostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: PostsEntity,



    @Column(nullable = false)
    val mediaUrl: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val mediaType: MediaType = MediaType.IMAGE
)


@Entity
@Table(name = "poll_posts")
class PollPostEntity(
    @Id
    val postId: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    val post: PostsEntity,

    val question: String,

    @ElementCollection
    @CollectionTable(
        name = "poll_options",
        joinColumns = [JoinColumn(name = "post_id")]
    )
    @Column(name = "option_text")
    val options: List<String> = listOf()
)


