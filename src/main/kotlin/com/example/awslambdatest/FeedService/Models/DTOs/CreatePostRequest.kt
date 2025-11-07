package com.iotabuild.campuscircle.FeedService.Models.DTOs

import com.iotabuild.campuscircle.FeedService.Models.Entity.FeedMode
import com.iotabuild.campuscircle.FeedService.Models.Entity.MediaType
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostType
import com.iotabuild.campuscircle.FeedService.Models.Entity.Visibility
import com.iotabuild.campuscircle.FeedService.Service.MediaPost
import com.iotabuild.campuscircle.FeedService.Service.Poll
import java.time.LocalDateTime

data class CreateMediaPostRequest(
    val text: String? = null,
    val mediaUrl: String? = null,
    val feedMode: FeedMode = FeedMode.OPEN,
    val visibility: Visibility = Visibility.USER,
    val postType: PostType = PostType.MEDIA,
    val mediaType: MediaType = MediaType.IMAGE,
    val campusId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

data class CreatePollPostRequest(
    val text: String? = null,
    val mediaUrl: String? = null,
    val feedMode: FeedMode = FeedMode.OPEN,
    val visibility: Visibility = Visibility.USER,
    val postType: PostType = PostType.POLL,
    val campusId: String? = null,
    val pollOptions: List<String>? = null
)

data class PostResponse(
    val postId: Long,
    val authorDetails: AuthorDetails? =null,
    val createdAt: Long? =null,
    val updatedAt: Long? =null,
    val visibility: Visibility,
    val postType: PostType,
    val text: String? = null,
    val poll: Poll? = null,
    val mediaPost: List<MediaPost> = emptyList(),
    val feedMode: FeedMode,
    val likes: Long,
    val isLiked: Boolean =false,
    val comments: Long
)

data class CreateCommentRequest(val content: String)

data class AuthorDetails(
    val authorId: String,
    val authorName: String ? = null,
    val authorImage: String? = null,
    val authorTagline: String? = null,
    val isVerified: Boolean ? = null,
    val isCurrentUser: Boolean ?= null

)