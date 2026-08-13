package com.iotabuild.campuscircle.Notification.strategy

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.Notification.event.PostLikedEvent
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.springframework.stereotype.Component

@Component
class LikeNotificationStrategy(

    private val userQueryService: UserQueryService

) : NotificationStrategy {

    override fun supports(
        event: NotificationEvent
    ) =
        event is PostLikedEvent

    override fun create(
        event: NotificationEvent
    ): NotificationEntity {

        event as PostLikedEvent

        val sender =
            userQueryService.getUserOrThrow(event.senderUid)

        return NotificationEntity(

            receiverUid = event.receiverUid,

            senderUid = event.senderUid,

            type = NotificationType.LIKE,

            entityType = EntityType.POST,

            entityId = event.postId,

            deepLink = "campuscircle://post/${event.postId}"
        )
    }
}