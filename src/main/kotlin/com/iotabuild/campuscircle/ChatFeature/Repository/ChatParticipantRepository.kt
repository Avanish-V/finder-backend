package com.iotabuild.campuscircle.ChatFeature.Repository

import com.iotabuild.campuscircle.ChatFeature.Models.ChatParticipantEntity
import com.iotabuild.campuscircle.ChatFeature.Models.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ChatParticipantRepository : JpaRepository<ChatParticipantEntity, UUID> {
    fun findByUserUid(uid: String): List<ChatParticipantEntity>
    fun findByRoomId(roomId: UUID): List<ChatParticipantEntity>
    fun findByRoomIdAndUserUid(roomId: UUID, uid: String): ChatParticipantEntity?

    @Query("""
        SELECT p1.room FROM ChatParticipantEntity p1 
        JOIN ChatParticipantEntity p2 ON p1.room.id = p2.room.id 
        WHERE p1.user.uid = :user1Id AND p2.user.uid = :user2Id
    """)
    fun findCommonRoom(user1Id: String, user2Id: String): ChatRoomEntity?
}
