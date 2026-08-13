package com.iotabuild.campuscircle.realtime.handler

import com.iotabuild.campuscircle.realtime.model.SocketEvent
import com.iotabuild.campuscircle.realtime.model.SocketEventType
import com.iotabuild.campuscircle.realtime.model.TypingPayload
import com.iotabuild.campuscircle.realtime.sender.SocketSender
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class TypingEventHandler(
    private val socketSender: SocketSender
) : SocketEventHandler {

    override val eventType = SocketEventType.USER_TYPING

    override fun handle(
        session: WebSocketSession,
        event: SocketEvent
    ) {
        val payload = event.payload as? TypingPayload ?: return
        socketSender.sendToUser(payload.recipientId, event)
    }
}
