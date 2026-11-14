package com.iotabuild.campuscircle.Notification.strategy

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import com.iotabuild.campuscircle.Notification.event.ConnectionRequestEvent
import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.springframework.stereotype.Component

@Component
class ConnectionRequestNotificationStrategy(
    private val userQueryService: UserQueryService
) : NotificationStrategy {

    override fun supports(event: NotificationEvent): Boolean =
        event is ConnectionRequestEvent

    override fun create(event: NotificationEvent): NotificationEntity {
        event as ConnectionRequestEvent
        val sender = userQueryService.getUserOrThrow(event.senderUid)

        return NotificationEntity(
            receiverUid = event.receiverUid,
            senderUid = event.senderUid,
            type = event.notificationType,
            entityType = event.entityType,
            entityId = event.requestId,
            deepLink = "campuscircle://profile/${event.senderUid}",
            note = event.note
        )
    }
}
