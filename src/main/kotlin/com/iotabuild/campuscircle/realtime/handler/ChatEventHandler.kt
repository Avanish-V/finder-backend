package com.iotabuild.campuscircle.realtime.handler

import com.iotabuild.campuscircle.ChatFeature.Service.ChatService
import com.iotabuild.campuscircle.realtime.mapper.PayloadMapper
import com.iotabuild.campuscircle.realtime.model.ChatMessageCreatedPayload
import com.iotabuild.campuscircle.realtime.model.MessageSentPayload
import com.iotabuild.campuscircle.realtime.model.SocketEvent
import com.iotabuild.campuscircle.realtime.model.SocketEventType
import com.iotabuild.campuscircle.realtime.sender.SocketSender
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class ChatEventHandler(

    private val chatService: ChatService,
    private val socketSender: SocketSender,
    private val payloadMapper: PayloadMapper

) : SocketEventHandler {

    override val eventType = SocketEventType.CHAT_MESSAGE
    override fun handle(
        session: WebSocketSession,
        event: SocketEvent
    ) {

        val senderUid =
            session.attributes["uid"] as String

        val payload = payloadMapper.chatSendMessage(event)

        val saved = chatService.saveMessage(
            payload.roomId,
            senderUid,
            payload.text ?: "",
            payload.attachmentUrl
        )

        val timestamp = saved.createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        val broadcastEvent = SocketEvent(
            eventType = SocketEventType.CHAT_MESSAGE,
            payload = ChatMessageCreatedPayload(
                messageId = saved.id.toString(),
                roomId = payload.roomId,
                senderUid = senderUid,
                text = saved.text,
                attachmentUrl = saved.attachmentUrl,
                timestamp = timestamp,
                read = saved.isRead
            )
        )

        // Send full message event to the recipient
        socketSender.sendToUser(payload.recipientId, broadcastEvent)

        // Send MESSAGE_SENT ack back to the sender so they get the server-assigned
        // messageId and timestamp (needed for deduplication and read-receipt tracking)
        socketSender.sendToUser(
            senderUid,
            SocketEvent(
                eventType = SocketEventType.MESSAGE_SENT,
                payload = MessageSentPayload(
                    localId  = "",          // client can pass a localId in the payload if needed
                    messageId = saved.id.toString()
                )
            )
        )
    }
}