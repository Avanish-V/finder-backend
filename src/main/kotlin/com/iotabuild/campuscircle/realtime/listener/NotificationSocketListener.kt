package com.iotabuild.campuscircle.realtime.listener

import com.iotabuild.campuscircle.Notification.event.NotificationCreatedEvent
import com.iotabuild.campuscircle.realtime.model.NotificationPayload
import com.iotabuild.campuscircle.realtime.model.SocketEventType
import com.iotabuild.campuscircle.realtime.sender.SocketSender
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NotificationSocketListener(

    private val socketSender: SocketSender

) {

    // Using @EventListener (not @TransactionalEventListener) because this listener is invoked
    // from within an @Async thread where there is no surrounding transaction context.
    // @TransactionalEventListener(AFTER_COMMIT) silently drops events in that scenario.
    @EventListener
    fun onNotificationCreated(

        event: NotificationCreatedEvent

    ) {

        socketSender.sendToUser(

            event.receiverUid,

            com.iotabuild.campuscircle.realtime.model.SocketEvent(

                eventType = SocketEventType.NOTIFICATION_CREATED,

                payload = NotificationPayload(
                    notificationId = event.notificationId
                )

            )

        )

    }

}