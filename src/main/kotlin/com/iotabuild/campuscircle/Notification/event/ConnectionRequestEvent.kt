package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import java.util.UUID

class ConnectionRequestEvent(
    override val receiverUid: String,
    override val senderUid: String,
    val requestId: UUID,
    val note: String? = null
) : NotificationEvent(
    receiverUid = receiverUid,
    senderUid = senderUid,
    notificationType = NotificationType.CONNECT_REQUEST,
    entityType = EntityType.USER,
    postId = requestId
)
