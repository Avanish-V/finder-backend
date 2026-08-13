package com.iotabuild.campuscircle.FeedService.Models.DTOs

import com.iotabuild.campuscircle.FeedService.Models.Entity.PostType

data class PostDataResponse(
    val postId: Long,
    val postType: PostType?,
    val authorId: String?,
    val likeCount: Long,
    val likedByCurrentUser: Boolean
)
