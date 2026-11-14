package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import java.util.UUID

class ChatMessageEvent(

    override val receiverUid: String,
    override val senderUid: String,
    override val postId: UUID

) : NotificationEvent(
    receiverUid = receiverUid,
    senderUid = senderUid,
    notificationType = NotificationType.MESSAGE,
    entityType = EntityType.CHAT,
    postId = postId
)