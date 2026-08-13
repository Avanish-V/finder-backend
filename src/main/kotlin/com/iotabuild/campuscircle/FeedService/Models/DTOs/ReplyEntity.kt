package com.iotabuild.campuscircle.FeedService.Models.DTOs

import com.iotabuild.campuscircle.FeedService.Models.Entity.Visibility


// Request sent by mobile: { postId, caption, mediaUrl?, parentId? }
data class ReplyRequest(
    val postId: Long,
    val caption: String,                    // mobile sends "caption" (not "text")
    val mediaUrl: String? = null,           // nullable — mobile may omit it
    val parentId: Long? = null,             // nullable Long
    // visibility is NOT sent by the mobile — default to USER in service
)


// Response consumed by mobile: { replyId(String), postId(String), author, caption, mediaUrl?, isEdited, parentId?, updatedAt?, createdAt, children }
data class ReplyResponse(
    val replyId: String,                    // mobile expects String
    val postId: String,                     // mobile expects String
    val author: AuthorDetails,
    val caption: String,                    // mobile expects "caption" (not "text")
    val mediaUrl: String? = null,
    val isEdited: Boolean = false,
    val parentId: Long? = null,
    val updatedAt: Long? = null,
    val createdAt: Long,
    val children: List<ReplyResponse> = emptyList(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class Actions(
    var isLiked: Boolean = false,
    val likesCount: Int = 0,
    val replies: List<String> = emptyList(),
    val replyCount: Int = 0
)