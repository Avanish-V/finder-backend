package com.iotabuild.campuscircle.FeedService.Models.DTOs

import java.time.Instant

data class PostLikeResponse(
    val success: Boolean,
    val message: String,
    val liked: Boolean,
    val postId: Long,
    val likeCount: Long
)

data class ToggleLikeResponse(
    val liked: Boolean,
    val message: String,
    val postId: Long,
    val likeCount: Long
)

data class LikeStatusResponse(
    val postId: Long,
    val liked: Boolean
)

data class BulkLikeStatusResponse(
    val likedPostIds: Set<Long>
)

data class PostLikeUserResponse(
    val userId: Long,
    val likedAt: Instant
)

data class UserLikedPostResponse(
    val postId: Long,
    val likedAt: Instant,
    val postContent: String?,
    val postLikeCount: Long
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)