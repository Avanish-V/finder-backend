package com.iotabuild.campuscircle.FeedService.Repository

import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostFilterType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomPostsRepository {

    fun findPostsWithLikeStatus(
        filterType: PostFilterType,
        filterValue: String,
        currentUserId: String,
        pageable: Pageable
    ): Page<Map<String, Any>>

    fun findSinglePostWithLikeStatus(
        postId: Long,
        currentUserId: String
    ): Map<String, Any>?

}
