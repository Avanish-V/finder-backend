package com.iotabuild.campuscircle.ChatFeature.Models

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "chat_messages")
class ChatMessageEntity(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: ChatRoomEntity = ChatRoomEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: UserEntity = UserEntity(),

    @Column(name = "text", nullable = false, length = 5000)
    var text: String = "",

    @Column(name = "attachment_url", length = 2048)
    var attachmentUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false
)
