package com.iotabuild.campuscircle.FeedService.Models.DTOs

import java.time.Instant
import java.util.UUID

data class ReplyResponse(
    val id: UUID,
    val content: String,
    val imageUrl: String?,
    val author: AuthorDetails,
    val mentionedUser: MentionedUser?,
    val parentReplyId: UUID?,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val likesCount: Long,
    val isLiked: Boolean,
    val childCount: Long,
    val isAuthor: Boolean,
    val deleted: Boolean
)