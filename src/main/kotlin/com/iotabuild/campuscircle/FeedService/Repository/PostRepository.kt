package com.iotabuild.campuscircle.FeedService.Repository

import com.iotabuild.campuscircle.FeedService.Models.Entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface PostsRepository : JpaRepository<PostsEntity, Long>, CustomPostsRepository{

    fun findAllByAuthorUid(uid: String): List<PostsEntity>


    fun findByPostId(postId: Long): PostsEntity

    @Query(
        value = """
        SELECT
          p.post_id,
          p.author_id,
          p.created_at,
          p.updated_at,
          p.post_type,
          p.like_count,
          p.reply_count,
          p."caption" AS caption,
          CASE WHEN pl.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_liked
        FROM posts p
        LEFT JOIN post_likes pl
          ON p.post_id = pl.post_id
          AND pl.user_id::TEXT = :currentUserId
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
interface PollOptionRepository : JpaRepository<PollOptionEntity, Long>

@Repository
interface PollVoteRepository : JpaRepository<PollVoteEntity, Long> {
    fun findByPollPostIdAndUserUid(postId: Long, userUid: String): PollVoteEntity?
}

@Repository
interface PostLikeRepository : JpaRepository<PostLikeEntity, Long>{
    fun findByUserUidAndPostPostId(userUid: String, postId: Long): PostLikeEntity?
    fun countByPostPostId(postId: Long): Long
}
