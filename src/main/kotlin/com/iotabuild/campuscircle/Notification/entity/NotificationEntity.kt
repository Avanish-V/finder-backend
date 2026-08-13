package com.iotabuild.campuscircle.Notification.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "notifications",
    indexes = [
        Index(name = "idx_receiver", columnList = "receiver_uid"),
        Index(name = "idx_receiver_read", columnList = "receiver_uid,is_read"),
        Index(name = "idx_receiver_created", columnList = "receiver_uid,created_at")
    ]
)
class NotificationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "receiver_uid", nullable = false)
    val receiverUid: String,

    @Column(name = "sender_uid", nullable = false)
    val senderUid: String,

    @Enumerated(EnumType.STRING)
    val type: NotificationType,

    @Enumerated(EnumType.STRING)
    val entityType: EntityType,

    val entityId: Long,

    val deepLink: String? = null,

    @Column(name = "is_read")
    var isRead: Boolean = false,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now()
)