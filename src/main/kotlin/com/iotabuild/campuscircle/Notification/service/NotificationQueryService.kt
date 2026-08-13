package com.iotabuild.campuscircle.Notification.service

import com.iotabuild.campuscircle.Notification.dto.NotificationResponse
import org.springframework.data.domain.Page

interface NotificationQueryService {

    fun getNotifications(
        uid: String,
        page: Int,
        size: Int
    ): Page<NotificationResponse>

    fun unreadCount(uid: String): Long

    fun getNotification(
        uid: String,
        notificationId: Long
    ): NotificationResponse
}