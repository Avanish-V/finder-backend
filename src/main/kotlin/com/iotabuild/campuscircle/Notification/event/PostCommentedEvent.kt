package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType

class PostCommentedEvent(

    override val receiverUid: String,

    override val senderUid: String,

    override val postId: Long

) : NotificationEvent(

    receiverUid = receiverUid,

    senderUid = senderUid,

    notificationType = NotificationType.COMMENT,

    entityType = EntityType.POST,

    postId = postId
)
