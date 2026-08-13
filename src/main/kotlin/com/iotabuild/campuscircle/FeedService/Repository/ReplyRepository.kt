package com.iotabuild.campuscircle.FeedService.Repository

import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyLikeEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface ReplyRepository : JpaRepository<ReplyEntity, Long>{
    fun findByPostId(postId: Long): List<ReplyEntity>
    fun findByPostIdAndParentId(postId: Long,parentId: Long): List<ReplyEntity>
    @Modifying
    @Transactional
    @Query("UPDATE ReplyEntity r SET r.likes = r.likes + 1 WHERE r.id = :replyId")
    fun likeIncrement(@Param("replyId") replyId: Long): Int

    @Modifying
    @Transactional
    @Query("UPDATE ReplyEntity r SET r.likes = CASE WHEN r.likes > 0 THEN r.likes - 1 ELSE 0 END WHERE r.id = :replyId")
    fun likeDecrement(@Param("replyId") replyId: Long): Int
}

interface LikeReplyRepository: JpaRepository<ReplyLikeEntity, Long>{
    fun findAllByUserIdAndReply_ReplyIdIn(userId: String, replyIds: List<Long>): List<ReplyLikeEntity>
    fun existsByUserIdAndReply_ReplyId(userId: String, replyId: Long): Boolean

    fun deleteByUserIdAndReply_ReplyId(userId: String, replyId: Long)

    fun countByReply_ReplyId(replyId: Long): Int

    @Modifying
    @Transactional
    @Query("DELETE FROM ReplyLikeEntity r WHERE r.reply.replyId IN :replyIds")
    fun deleteByReplyReplyIdIn(@Param("replyIds") replyIds: List<Long>)
}