package com.iotabuild.campuscircle.Notification.listener

import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.Notification.service.NotificationCommandService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NotificationListener(

    private val notificationCommandService: NotificationCommandService

) {

    @Async
    @EventListener
    fun onNotificationEvent(event: NotificationEvent) {
        notificationCommandService.handle(event)
    }
}
