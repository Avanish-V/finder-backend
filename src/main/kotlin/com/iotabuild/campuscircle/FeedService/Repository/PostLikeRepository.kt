package com.iotabuild.campuscircle.FeedService.Repository


import com.iotabuild.campuscircle.FeedService.Models.DTOs.UpvotedUserResponse
import com.iotabuild.campuscircle.FeedService.Models.DTOs.UpvoterWithPostProjection
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostLikeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PostLikeRepository : JpaRepository<PostLikeEntity, UUID> {
    fun existsByPostIdAndUserUid(
        postId: UUID,
        userUid: String
    ): Boolean

    fun deleteByPostIdAndUserUid(
        postId: UUID,
        userUid: String
    ): Long

    fun findAllByUserUidAndPostIdIn(
        userUid: String,
        postIds: List<UUID>
    ): List<PostLikeEntity>

    @Query("""
        SELECT new com.iotabuild.campuscircle.FeedService.Models.DTOs.UpvotedUserResponse(
            u.uid,
            u.name,
            u.image,
            u.summary,
            u.tagline
        )
        FROM PostLikeEntity pl
        JOIN pl.user u
        WHERE pl.post.id = :postId
        ORDER BY pl.createdAt DESC
    """)
    fun findUpvotersByPostId(@Param("postId") postId: UUID, pageable: Pageable): Page<UpvotedUserResponse>

    @Query("""
        SELECT new com.iotabuild.campuscircle.FeedService.Models.DTOs.UpvotedUserResponse(
            u.uid,
            u.name,
            u.image,
            u.summary,
            u.tagline
        )
        FROM PostLikeEntity pl
        JOIN pl.user u
        WHERE pl.post.id = :postId
        ORDER BY pl.createdAt DESC
    """)
    fun findAllUpvotersByPostId(@Param("postId") postId: UUID): List<UpvotedUserResponse>

    @Query("""
        SELECT pl.post.id AS postId,
               u.uid AS uid,
               u.name AS name,
               u.image AS image,
               u.summary AS about,
               u.tagline AS tagline
        FROM PostLikeEntity pl
        JOIN pl.user u
        WHERE pl.post.id IN :postIds
        ORDER BY pl.createdAt DESC
    """)
    fun findUpvotersByPostIdIn(@Param("postIds") postIds: Collection<UUID>): List<UpvoterWithPostProjection>
}
