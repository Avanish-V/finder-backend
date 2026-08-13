package com.iotabuild.campuscircle.FeedService.Models.DTOs

import com.iotabuild.campuscircle.FeedService.Models.Entity.MediaType
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostType
import com.iotabuild.campuscircle.FeedService.Models.Entity.Visibility
import com.iotabuild.campuscircle.FeedService.Service.MediaPost
import com.iotabuild.campuscircle.FeedService.Service.Poll
import java.time.LocalDateTime

data class CreateMediaPostRequest(
    val text: String? = null,
    val mediaUrl: String? = null,
    val visibility: Visibility = Visibility.USER,
    val postType: PostType = PostType.MEDIA,
    val mediaType: MediaType = MediaType.IMAGE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

data class CreatePollPostRequest(
    val text: String? = null,
    val mediaUrl: String? = null,
    val visibility: Visibility = Visibility.USER,
    val postType: PostType = PostType.POLL,
    val pollOptions: List<String>? = null
)

data class PostResponse(
    val postId: Long,
    val authorDetails: AuthorDetails? =null,
    val createdAt: Long? =null,
    val updatedAt: Long? =null,
    val postType: PostType,
    val text: String? = null,
    val poll: Poll? = null,
    val mediaPost: List<MediaPost> = emptyList(),
    val likes: Long,
    val isLiked: Boolean =false,
    val comments: Long
)

data class CreateCommentRequest(val content: String)

data class AuthorDetails(
    val authorId: String = "",
    val authorName: String = "",
    val authorImage: String = "",
    val authorTagline: String = "",
    val isVerified: Boolean = false,
    val isCurrentUser: Boolean = false

)