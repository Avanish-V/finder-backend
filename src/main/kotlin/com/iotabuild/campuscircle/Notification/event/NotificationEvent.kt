package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType

abstract class NotificationEvent(

    open val receiverUid: String,

    open val senderUid: String,

    open val notificationType: NotificationType,

    open val entityType: EntityType,

    open val postId: Long
)