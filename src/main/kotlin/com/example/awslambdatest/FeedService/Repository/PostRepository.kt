package com.iotabuild.campuscircle.FeedService.Repository

import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostWithLikeProjection
import com.iotabuild.campuscircle.FeedService.Models.Entity.FeedMode
import com.iotabuild.campuscircle.FeedService.Models.Entity.MediaPostEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.PollPostEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostLikeEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostsEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.TextPostEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface PostsRepository : JpaRepository<PostsEntity, Long>, CustomPostsRepository{

    fun findAllByAuthorUid(uid: String): List<PostsEntity>

    fun findAllByFeedMode(feedMode: FeedMode,pageable: Pageable): Page<PostsEntity>

    fun findByPostId(postId: Long): PostsEntity

    @Query(
        value = """
        SELECT
          p.post_id,
          p.author_id,
          p.created_at,
          p.updated_at,
          p.visibility,
          p.post_type,
          p.feed_mode,
          p.like_count,
          p.reply_count,
          p."content" AS content,
          CASE WHEN pl.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_liked
        FROM posts p
        LEFT JOIN post_likes pl
          ON p.post_id = pl.post_id
          AND pl.user_id = :currentUserId
        WHERE p.post_id = :postId
        LIMIT 1
    """,
        nativeQuery = true
    )

    fun findPostByIdWithLikeStatus(
        @Param("postId") postId: Long,
        @Param("currentUserId") currentUserId: String
    ): Map<String, Any>?

}


@Repository
interface TextPostRepository : JpaRepository<TextPostEntity, Long>

@Repository
interface MediaPostRepository : JpaRepository<MediaPostEntity, Long>{
    fun findAllByPostPostId(postId: Long): List<MediaPostEntity>
}

@Repository
interface PollPostRepository : JpaRepository<PollPostEntity, Long>

@Repository
interface PostLikeRepository : JpaRepository<PostLikeEntity, Long>{
    fun findByUserUidAndPostPostId(userUid: String, postId: Long): PostLikeEntity?
    fun countByPostPostId(postId: Long): Long
}
