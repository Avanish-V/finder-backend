package com.iotabuild.campuscircle.Notification.strategy

import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import com.iotabuild.campuscircle.Notification.event.NotificationEvent

interface NotificationStrategy {

    /**
     * Can this strategy handle this event?
     */
    fun supports(event: NotificationEvent): Boolean

    /**
     * Convert event -> Notification entity
     */
    fun create(event: NotificationEvent): NotificationEntity
}