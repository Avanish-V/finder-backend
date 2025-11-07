package com.iotabuild.campuscircle.FeedService.Models.Entity

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "reply")
data class ReplyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    val replyId: Long? = null,

    val postId: Long,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: UserEntity = UserEntity(),

    @Enumerated(EnumType.STRING)
    val visibility: Visibility = Visibility.USER,

    val text: String,
    val parentId: Long? = null,
    val mediaUrl: String,

    @Column(nullable = false)
    var likes: Long = 0,
    @Column(nullable = false)
    val replies: Long = 0
)

