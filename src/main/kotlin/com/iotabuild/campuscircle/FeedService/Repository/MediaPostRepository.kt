package com.iotabuild.campuscircle.FeedService.Repository

import com.iotabuild.campuscircle.FeedService.Models.Entity.FeedMediaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MediaPostRepository : JpaRepository<FeedMediaEntity, UUID> {
    fun findAllByPostIdIn(
        postIds: List<UUID>
    ): List<FeedMediaEntity>

    fun findAllByPostIdOrderByPositionAsc(
        postId: UUID
    ): List<FeedMediaEntity>

    fun deleteByPostId(
        postId: UUID
    )
}