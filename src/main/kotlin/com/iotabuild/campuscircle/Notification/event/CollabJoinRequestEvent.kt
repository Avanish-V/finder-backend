package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import java.util.UUID

class CollabJoinRequestEvent(
    override val receiverUid: String,
    override val senderUid: String,
    val collabId: UUID,
    val collabTitle: String
) : NotificationEvent(
    receiverUid = receiverUid,
    senderUid = senderUid,
    notificationType = NotificationType.COLLABORATION_REQUEST,
    entityType = EntityType.COLLABORATION,
    postId = collabId
)
