package com.iotabuild.campuscircle.FeedService.Models.DTOs

import com.iotabuild.campuscircle.FeedService.Models.Entity.Visibility


data class ReplyRequest(

    val postId: Long,
    val visibility: Visibility,
    val text: String,
    val mediaUrl: String,
    val parentId: Long?=null,
)


data class ReplyResponse(

    val replyId: Long?,
    val postId: Long,
    val author: AuthorDetails,
    val visibility: Visibility,
    val text: String,
    val mediaUrl: String,
    val isEdited: Boolean = false,
    val parentId: Long? = null,
    val updatedAt: Long,
    val createdAt: Long,
    val actions: Actions,
    val children: List<ReplyResponse> = emptyList()

)

data class Actions(
    var isLiked: Boolean = false,
    val likesCount: Int = 0,
    val replies: List<String> = emptyList(),
    val replyCount: Int = 0
)