package com.iotabuild.campuscircle.Notification.pushNotiification

import com.iotabuild.campuscircle.Notification.entity.NotificationType
import com.iotabuild.campuscircle.Notification.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.iotabuild.campuscircle.Common.exception.NotificationNotFoundException
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
class FirebasePushNotificationService(
    private val notificationRepository: NotificationRepository,
    private val userQueryService: UserQueryService,
    private val firebaseMessaging: FirebaseMessaging
) : PushNotificationService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(
        notificationId: Long
    ) {

        val notification =
            notificationRepository.findById(notificationId)
                .orElseThrow {
                    NotificationNotFoundException(notificationId)
                }

        val receiver = userQueryService.getUserOrThrow(notification.receiverUid)

        val token = receiver.fcmToken ?: return

        // Build a human-readable title and body from the notification type
        val sender = userQueryService.getUserOrThrow(notification.senderUid)
        val senderName = sender.name ?: "Someone"
        val (title, body) = buildTitleAndBody(notification.type, senderName)

        val message =
            Message.builder()
                .setToken(token)
                .setNotification(
                    com.google.firebase.messaging.Notification
                        .builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .putData(
                    "notificationId",
                    notification.id.toString()
                )
                .putData(
                    "type",
                    notification.type.name
                )
                .putData(
                    "entityType",
                    notification.entityType.name
                )
                .putData(
                    "entityId",
                    notification.entityId.toString()
                )
                .putData(
                    "deepLink",
                    notification.deepLink ?: ""
                )
                .build()
        try {

            firebaseMessaging.send(message)

        } catch (ex: FirebaseMessagingException) {

            when (ex.messagingErrorCode) {

                MessagingErrorCode.UNREGISTERED -> {
                    log.warn("FCM token unregistered for uid=${notification.receiverUid}, clearing token")
                    handleInvalidToken(notification.receiverUid)
                }

                MessagingErrorCode.INVALID_ARGUMENT -> {
                    log.error("FCM invalid argument for notificationId=$notificationId: ${ex.message}")
                }

                else -> {
                    log.error("FCM send failed for notificationId=$notificationId [${ex.messagingErrorCode}]: ${ex.message}")
                }
            }
        }
    }

    /** Returns a (title, body) pair based on the notification type and sender's name. */
    private fun buildTitleAndBody(type: NotificationType, senderName: String): Pair<String, String> =
        when (type) {
            NotificationType.LIKE    -> "New Like" to "$senderName liked upvoted post"
            NotificationType.COMMENT -> "New Comment" to "$senderName commented on your post"
            NotificationType.REPLY   -> "New Reply" to "$senderName replied to your comment"
            NotificationType.FOLLOW  -> "New Follower" to "$senderName started following you"
            NotificationType.MESSAGE -> "New Message" to "$senderName sent you a message"
            NotificationType.MENTION -> "You were mentioned" to "$senderName mentioned you in a post"
            NotificationType.SYSTEM  -> "Notification" to "You have a new notification"
        }

    private fun handleInvalidToken(
        receiverUid: String
    ) {
        val user = userQueryService.getUserOrThrow(receiverUid)
        user.fcmToken = null
    }
}