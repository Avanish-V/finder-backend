package com.iotabuild.campuscircle.Notification.dto

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import java.time.Instant

data class NotificationResponse(
    val id: Long,
    val entityId: Long,
    val deepLink: String?= null,
    val senderUid: String,
    val senderName: String,
    val senderImage: String? = null,
    val entityType: EntityType,
    val type: NotificationType,
    val isRead: Boolean,
    val createdAt: String
)
