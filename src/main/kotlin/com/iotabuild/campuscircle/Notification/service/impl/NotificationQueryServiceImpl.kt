package com.iotabuild.campuscircle.Notification.service.impl

import com.iotabuild.campuscircle.Common.exception.NotificationNotFoundException
import com.iotabuild.campuscircle.Common.exception.UserNotFoundException
import com.iotabuild.campuscircle.Notification.repository.NotificationRepository
import com.iotabuild.campuscircle.Notification.dto.NotificationResponse
import com.iotabuild.campuscircle.Notification.service.NotificationQueryService
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NotificationQueryServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val userQueryService: UserQueryService

) : NotificationQueryService {

    override fun getNotifications(

        uid: String,

        page: Int,

        size: Int

    ): Page<NotificationResponse> {

        val pageable = PageRequest.of(

            page,

            size,

            Sort.by(
                Sort.Direction.DESC,
                "createdAt"
            )
        )

        val notifications =  notificationRepository.findByReceiverUidOrderByCreatedAtDesc(uid, pageable)

        val senderIds = notifications.content
            .map { it.senderUid }
            .distinct()



        val users = userRepository.findAllById(senderIds)

        val userMap = users.associateBy { it.uid }

        return notifications.map { notification ->

            val sender = userMap[notification.senderUid]
                ?: throw UserNotFoundException(notification.senderUid)

            NotificationResponse(
                id = notification.id,
                senderUid = sender.uid,
                senderName = sender.name,
                type = notification.type,
                entityType = notification.entityType,
                entityId = notification.entityId,
                isRead = notification.isRead,
                createdAt = notification.createdAt.toString(),
                deepLink = notification.deepLink,
                senderImage = sender.image
            )
        }


    }

    override fun unreadCount(

        uid: String

    ): Long {

        return notificationRepository

            .countByReceiverUidAndIsReadFalse(uid)
    }

    override fun getNotification(
        uid: String,
        notificationId: Long
    ): NotificationResponse {

        val notification = notificationRepository
            .findByIdAndReceiverUid(notificationId, uid)
            ?: throw NotificationNotFoundException(notificationId)

        val sender = userQueryService.getUserOrThrow(notification.senderUid)

        return NotificationResponse(
            id = notification.id,
            entityId = notification.entityId,
            deepLink = notification.deepLink,
            senderUid = sender.uid,
            senderName = sender.name,
            senderImage = sender.image,
            entityType = notification.entityType,
            type = notification.type,
            isRead = notification.isRead,
            createdAt = notification.createdAt.toString()
        )
    }

}