package com.iotabuild.campuscircle.Notification.strategy

import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import com.iotabuild.campuscircle.Notification.event.CollabFeedbackEvent
import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import org.springframework.stereotype.Component

@Component
class CollabFeedbackNotificationStrategy : NotificationStrategy {

    override fun supports(event: NotificationEvent): Boolean =
        event is CollabFeedbackEvent

    override fun create(event: NotificationEvent): NotificationEntity {
        event as CollabFeedbackEvent

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
