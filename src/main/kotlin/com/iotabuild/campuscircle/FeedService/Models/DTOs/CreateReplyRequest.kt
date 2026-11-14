package com.iotabuild.campuscircle.FeedService.Models.DTOs

import java.util.UUID

data class CreateReplyRequest(
    val content: String = "",
    val imageUrl: String? = null,
    val parentReplyId: UUID? = null,
    val mentionedUserId: String? = null
)