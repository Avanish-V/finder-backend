package com.iotabuild.campuscircle.Notification.service

import com.iotabuild.campuscircle.Notification.event.NotificationEvent

interface NotificationCommandService {

    fun handle(event: NotificationEvent)

    fun markRead(uid: String, notificationId: Long)

    fun markAllRead(uid: String)
}