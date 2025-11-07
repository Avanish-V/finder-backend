package com.iotabuild.campuscircle.FeedService.Models.DTOs

import com.iotabuild.campuscircle.FeedService.Models.Entity.PostType

interface PostWithLikeProjection {
    val postId: Long
    val postType: PostType?
    val authorId: String?
    val likeCount: Long?
    val text: String?
    val likedByCurrentUser: Boolean
}
