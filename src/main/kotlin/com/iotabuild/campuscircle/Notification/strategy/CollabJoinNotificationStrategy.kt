package com.iotabuild.campuscircle.Notification.strategy

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import com.iotabuild.campuscircle.Notification.event.CollabJoinRequestEvent
import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.springframework.stereotype.Component

@Component
class CollabJoinNotificationStrategy(
    private val userQueryService: UserQueryService
) : NotificationStrategy {

    override fun supports(event: NotificationEvent): Boolean =
        event is CollabJoinRequestEvent

    override fun create(event: NotificationEvent): NotificationEntity {
        event as CollabJoinRequestEvent
        val sender = userQueryService.getUserOrThrow(event.senderUid)

        return NotificationEntity(
            receiverUid = event.receiverUid,
            senderUid = event.senderUid,
            type = event.notificationType,
            entityType = event.entityType,
            entityId = event.postId,
            deepLink = "campuscircle://collab/${event.collabId}"
        )
    }
}
