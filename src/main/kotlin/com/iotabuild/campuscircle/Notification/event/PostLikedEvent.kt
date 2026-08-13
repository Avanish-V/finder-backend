package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType

class PostLikedEvent(

    override val receiverUid: String,

    override val senderUid: String,

    override val postId: Long

) : NotificationEvent(

    receiverUid = receiverUid,

    senderUid = senderUid,

    notificationType = NotificationType.LIKE,

    entityType = EntityType.POST,

    postId = postId
)