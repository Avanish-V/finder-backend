package com.iotabuild.campuscircle.Notification.event

data class NotificationCreatedEvent(
    val receiverUid: String,
    val notificationId: Long

)