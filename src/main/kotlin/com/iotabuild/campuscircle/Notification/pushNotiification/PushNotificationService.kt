package com.iotabuild.campuscircle.Notification.pushNotiification

import org.springframework.stereotype.Service


@Service
interface PushNotificationService {

    fun send(
        notificationId: Long
    )
}