package com.iotabuild.campuscircle.Common.exception

class NotificationNotFoundException(
    notificationId: Long
) : RuntimeException(
    "Notification not found with id: $notificationId"
)