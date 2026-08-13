package com.iotabuild.campuscircle.realtime.handler

import com.iotabuild.campuscircle.realtime.model.SocketEvent
import com.iotabuild.campuscircle.realtime.model.SocketEventType
import com.iotabuild.campuscircle.realtime.model.PresencePayload
import com.iotabuild.campuscircle.realtime.sender.SocketSender
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class UserOnlineEventHandler(
    private val socketSender: SocketSender
) : SocketEventHandler {

    override val eventType = SocketEventType.USER_ONLINE

    override fun handle(
        session: WebSocketSession,
        event: SocketEvent
    ) {
        val payload = event.payload as? PresencePayload ?: return
        socketSender.sendToUser(payload.recipientId, event)
    }
}

@Component
class UserOfflineEventHandler(
    private val socketSender: SocketSender
) : SocketEventHandler {

    override val eventType = SocketEventType.USER_OFFLINE

    override fun handle(
        session: WebSocketSession,
        event: SocketEvent
    ) {
        val payload = event.payload as? PresencePayload ?: return
        socketSender.sendToUser(payload.recipientId, event)
    }
}
