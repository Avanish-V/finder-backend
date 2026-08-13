package com.iotabuild.campuscircle.ChatFeature.Repository

import com.iotabuild.campuscircle.ChatFeature.Models.ChatMessageEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, UUID> {
    fun findByRoomIdOrderByCreatedAtDesc(roomId: UUID, pageable: Pageable): List<ChatMessageEntity>
    fun findFirstByRoomIdOrderByCreatedAtDesc(roomId: UUID): ChatMessageEntity?
    fun countByRoomIdAndSenderUidNotAndIsReadFalse(roomId: UUID, senderUid: String): Int

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessageEntity m SET m.isRead = true WHERE m.room.id = :roomId AND m.sender.uid != :userId AND m.isRead = false")
    fun markMessagesAsRead(roomId: UUID, userId: String): Int
}
