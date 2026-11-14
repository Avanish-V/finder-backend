package com.iotabuild.campuscircle.CollabService.Models

data class CollabFeedbackRequest(
    val content: String = "",
    val parentFeedbackId: String? = null
)

data class CollabAuthorDetails(
    val authorId: String = "",
    val authorName: String = "",
    val authorImage: String? = null,
    val authorTagline: String? = null,
    val isProjectLead: Boolean = false,
    val isCurrentUser: Boolean = false
)

data class CollabFeedbackResponse(
    val id: String = "",
    val collabId: String = "",
    val content: String = "",
    val author: CollabAuthorDetails = CollabAuthorDetails(),
    val parentFeedbackId: String? = null,
    val createdAt: String = "",
    val timeAgo: String = "Just now",
    val childCount: Int = 0
)
