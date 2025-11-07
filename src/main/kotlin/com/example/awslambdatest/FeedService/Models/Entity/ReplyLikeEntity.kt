package com.iotabuild.campuscircle.FeedService.Models.Entity

import jakarta.persistence.*

@Entity
@Table(
    name = "reply_like",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "reply_id"])]
)
data class ReplyLikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id", nullable = false)
    val reply: ReplyEntity
)

