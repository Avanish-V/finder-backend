package com.iotabuild.campuscircle.Notification.service.impl

import com.iotabuild.campuscircle.Notification.repository.NotificationRepository
import com.iotabuild.campuscircle.Common.exception.NotificationNotFoundException
import com.iotabuild.campuscircle.Notification.event.NotificationCreatedEvent
import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.Notification.factory.NotificationFactory
import com.iotabuild.campuscircle.Notification.service.NotificationCommandService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationCommandServiceImpl(

    private val notificationFactory: NotificationFactory,
    private val notificationRepository: NotificationRepository,
    private val publisher: ApplicationEventPublisher

) : NotificationCommandService {

    override fun handle(
        event: NotificationEvent
    ) {

        val strategy =
            notificationFactory.getStrategy(event)

        val notification =
            strategy.create(event)

        val savedNotification =
            notificationRepository.save(notification)

        publisher.publishEvent(
            NotificationCreatedEvent(

                receiverUid = savedNotification.receiverUid,

                notificationId = savedNotification.id

            )
        )
    }

    override fun markRead(
        uid: String,
        notificationId: Long
    ) {

        val notification =
            notificationRepository.findByIdAndReceiverUid(
                notificationId,
                uid
            ) ?: throw NotificationNotFoundException(notificationId)

        if (!notification.isRead) {
            notification.isRead = true
        }
    }

    override fun markAllRead(
        uid: String
    ) {

        notificationRepository.markAllRead(uid)
    }
}