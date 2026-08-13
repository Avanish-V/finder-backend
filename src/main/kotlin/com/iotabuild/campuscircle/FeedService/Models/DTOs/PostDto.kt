package com.iotabuild.campuscircle.FeedService.Models.DTOs

import com.iotabuild.campuscircle.FeedService.Models.Entity.MediaType
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostType
import com.iotabuild.campuscircle.FeedService.Models.Entity.Visibility
import java.time.LocalDateTime

data class PostDto(
    val postId: Long = 0,
    val authorId: String,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    val visibility: Visibility = Visibility.USER,
    val postType: PostType = PostType.MEDIA,
    val mediaType: MediaType = MediaType.IMAGE,
    val text: String?=null,
    val mediaUrl: String? = null
)
