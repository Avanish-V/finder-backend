package com.iotabuild.campuscircle.FeedService.Repository

import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyLikeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ReplyLikeRepository : JpaRepository<ReplyLikeEntity, UUID> {

    fun existsByUserUidAndReplyId(
        userUid: String,
        replyId: UUID
    ): Boolean

    fun deleteByUserUidAndReplyId(
        userUid: String,
        replyId: UUID
    ): Long

    fun findAllByUserUidAndReplyIdIn(
        userUid: String,
        replyIds: List<UUID>
    ): List<ReplyLikeEntity>

    @Query(
        """
        SELECT 
            rl.reply.id AS replyId,
            COUNT(rl.id) AS count
        FROM ReplyLikeEntity rl
        WHERE rl.reply.id IN :replyIds
        GROUP BY rl.reply.id
        """
    )
    fun countLikesByReplyIds(
        @Param("replyIds") replyIds: List<UUID>
    ): List<ReplyLikeCountProjection>
}
interface ReplyLikeCountProjection {
    val replyId: UUID
    val count: Long
}