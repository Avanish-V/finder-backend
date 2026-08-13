package com.iotabuild.campuscircle.Notification.strategy

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.Notification.event.PostCommentedEvent
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.springframework.stereotype.Component

@Component
class CommentNotificationStrategy(
    private val userQueryService: UserQueryService
) : NotificationStrategy {

    override fun supports(
        event: NotificationEvent
    ) =
        event is PostCommentedEvent

    override fun create(
        event: NotificationEvent
    ): NotificationEntity {

        event as PostCommentedEvent

        val sender = userQueryService.getUserOrThrow(event.senderUid)

        return NotificationEntity(

            receiverUid = event.receiverUid,

            senderUid = event.senderUid,

            type = NotificationType.COMMENT,

            entityType = EntityType.POST,

            entityId = event.postId,

            deepLink = "campuscircle://post/${event.postId}"
        )
    }
}