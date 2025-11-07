package com.iotabuild.campuscircle.UserSearch.Repository

import com.iotabuild.campuscircle.FeedService.Models.Entity.FeedMode
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostsEntity
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import org.apache.catalina.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserSearchRepository : JpaRepository<UserEntity, UUID>{


    @Query(
        value = """
            SELECT * FROM users
            WHERE lower(name) = lower(:q)  -- exact matches first
               OR similarity(lower(name), lower(:q)) > 0.3  -- fuzzy threshold
            ORDER BY 
                CASE WHEN lower(name) = lower(:q) THEN 1 ELSE 0 END DESC,  -- exact matches first
                similarity(lower(name), lower(:q)) DESC
        """,
        countQuery = """
            SELECT count(*) FROM users
            WHERE lower(name) = lower(:q)
               OR similarity(lower(name), lower(:q)) > 0.3
        """,
        nativeQuery = true
    )
    fun searchUsers(@Param("q") query: String, pageable: Pageable): Page<UserEntity>


}

interface PostSearchRepository : JpaRepository<PostsEntity, Long>{

    @Query(
        """
    SELECT p FROM PostsEntity p
    LEFT JOIN p.mediaList m
    WHERE p.feedMode = :feedMode
    AND (
        LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(p.author.name) LIKE LOWER(CONCAT('%', :query, '%'))
    )
    GROUP BY p
    ORDER BY p.createdAt DESC
    """
    )
    fun searchPostsWithMedia(
        @Param("query") query: String,
        @Param("feedMode") feedMode: FeedMode = FeedMode.OPEN,
        pageable: Pageable
    ): Page<PostsEntity>




}