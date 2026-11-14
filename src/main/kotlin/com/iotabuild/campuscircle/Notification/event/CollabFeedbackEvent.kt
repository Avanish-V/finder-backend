package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.Notification.entity.EntityType
import com.iotabuild.campuscircle.Notification.entity.NotificationType
import java.util.UUID

class CollabFeedbackEvent(
    override val receiverUid: String,
    override val senderUid: String,
    val collabId: UUID,
    val collabTitle: String,
    val feedbackId: UUID,
    val isReply: Boolean
) : NotificationEvent(
    receiverUid = receiverUid,
    senderUid = senderUid,
    notificationType = if (isReply) NotificationType.REPLIED_ON_FEEDBACK else NotificationType.PROJECT_FEEDBACK,
    entityType = EntityType.COLLABORATION,
    postId = collabId
)
