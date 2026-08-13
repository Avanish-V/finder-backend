package com.iotabuild.campuscircle.ChatFeature.Models

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "chat_participants")
class ChatParticipantEntity(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: ChatRoomEntity = ChatRoomEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity = UserEntity(),

    @Column(name = "last_read_at", nullable = false)
    var lastReadAt: LocalDateTime = LocalDateTime.now()
)
