package com.iotabuild.campuscircle.CollabService.Models

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "collab_feedbacks",
    indexes = [
        Index(name = "idx_collab_feedbacks_collab_id", columnList = "collab_id"),
        Index(name = "idx_collab_feedbacks_parent_id", columnList = "parent_feedback_id"),
        Index(name = "idx_collab_feedbacks_author_id", columnList = "author_id"),
        Index(name = "idx_collab_feedbacks_created_at", columnList = "created_at")
    ]
)
class CollabFeedbackEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collab_id", nullable = false)
    var collab: CollabEntity = CollabEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: UserEntity = UserEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_feedback_id", nullable = true)
    var parentFeedback: CollabFeedbackEntity? = null,

    @Column(nullable = false, length = 2000)
    var content: String = "",

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
