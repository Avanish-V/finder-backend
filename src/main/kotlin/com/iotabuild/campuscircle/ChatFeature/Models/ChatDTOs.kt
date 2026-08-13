package com.iotabuild.campuscircle.ChatFeature.Models

data class UserChatsDTO(
    val roomId: String,
    val receiverId: String,
    val userName: String,
    val userImage: String?,
    val lastMessage: LastMessage
)

data class LastMessage(
    val lastMessage: String,
    val timeStamp: Long,
    val unreadCount: Int,
    val isRead: Boolean,
    val lastMessageBy: Boolean
)

data class ChatMessageDTO(
    val messageId: String,
    val senderId: String,
    val text: String,
    val attachmentUrl: String?,
    val timestamp: Long,
    val read: Boolean
)
