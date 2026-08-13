package com.iotabuild.campuscircle.Notification.pushNotiification

import com.iotabuild.campuscircle.Notification.event.NotificationCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class PushNotificationListener(
    private val pushNotificationService: PushNotificationService

) {
    @Async
    @EventListener
    fun onNotificationCreated(
        event: NotificationCreatedEvent
    ) {
        pushNotificationService.send(
            event.notificationId
        )
    }
}