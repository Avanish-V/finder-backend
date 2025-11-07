package com.iotabuild.campuscircle.FeedService.Repository

import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostFilterType
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CustomPostsRepositoryImpl(
    @PersistenceContext private val entityManager: EntityManager
) : CustomPostsRepository {

    /**
     * Reusable base SELECT for both single + multiple posts
     */
    private fun baseSelect(): String = """
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
      CASE WHEN pl.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_liked,
      p.content
    FROM posts p
    LEFT JOIN post_likes pl
      ON p.post_id = pl.post_id
      AND pl.user_id = :currentUserId
""".trimIndent()


    /**
     * Fetch posts (paginated) by filter
     */
    override fun findPostsWithLikeStatus(
        filterType: PostFilterType,
        filterValue: String,
        currentUserId: String,
        pageable: Pageable
    ): Page<Map<String, Any>> {

        val whereClause = when (filterType) {
            PostFilterType.FEED_MODE -> "WHERE p.feed_mode = :filterValue"
            PostFilterType.AUTHOR_ID -> "WHERE p.author_id = :filterValue"
        }

        val sql = """
            ${baseSelect()}
            $whereClause
            ORDER BY p.created_at DESC
            LIMIT ${pageable.pageSize} OFFSET ${pageable.offset}
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql)
        query.setParameter("currentUserId", currentUserId)
        query.setParameter("filterValue", filterValue)

        val resultList = query.resultList.map { row ->
            val arr = row as Array<Any>
            mapOf(
                "post_id" to (arr[0] as Number).toLong(),
                "author_id" to arr[1],
                "created_at" to arr[2],
                "updated_at" to arr[3],
                "visibility" to arr[4],
                "post_type" to arr[5],
                "feed_mode" to arr[6],
                "like_count" to arr[7],
                "reply_count" to arr[8],
                "is_liked" to arr[9],
                "content" to arr[10],
            )
        }

        val countSql = """
            SELECT COUNT(*) FROM posts p
            ${whereClause.replace("p.", "")}
        """.trimIndent()
        val countQuery = entityManager.createNativeQuery(countSql)
        countQuery.setParameter("filterValue", filterValue)
        val total = (countQuery.singleResult as Number).toLong()

        return PageImpl(resultList, pageable, total)
    }

    /**
     * Fetch single post
     */
    override fun findSinglePostWithLikeStatus(
        postId: Long,
        currentUserId: String
    ): Map<String, Any>? {
        val sql = """
            ${baseSelect()}
            WHERE p.post_id = :postId
            LIMIT 1
        """.trimIndent()

        val query = entityManager.createNativeQuery(sql)
        query.setParameter("postId", postId)
        query.setParameter("currentUserId", currentUserId)

        val result = query.resultList.firstOrNull() as? Array<Any> ?: return null

        return mapOf(
            "post_id" to (result[0] as Number).toLong(),
            "author_id" to result[1],
            "created_at" to result[2],
            "updated_at" to result[3],
            "visibility" to result[4],
            "post_type" to result[5],
            "feed_mode" to result[6],
            "like_count" to result[7],
            "reply_count" to result[8],
            "is_liked" to result[9],
            "content" to result[10],
        )
    }
}
