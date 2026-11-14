package com.iotabuild.campuscircle.Notification.strategy

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import com.iotabuild.campuscircle.Notification.event.ConnectionAcceptedEvent
import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.springframework.stereotype.Component

@Component
class ConnectionAcceptedNotificationStrategy(
    private val userQueryService: UserQueryService
) : NotificationStrategy {

    override fun supports(event: NotificationEvent): Boolean =
        event is ConnectionAcceptedEvent

    override fun create(event: NotificationEvent): NotificationEntity {
        event as ConnectionAcceptedEvent
        val sender = userQueryService.getUserOrThrow(event.senderUid)

        return NotificationEntity(
            receiverUid = event.receiverUid,
            senderUid = event.senderUid,
            type = NotificationType.MESSAGE,
            entityType = EntityType.CHAT,
            entityId = event.connectionId,
            deepLink = "campuscircle://chat/${event.senderUid}"
        )
    }
}
