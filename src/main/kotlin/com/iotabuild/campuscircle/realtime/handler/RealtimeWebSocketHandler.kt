package com.iotabuild.campuscircle.realtime.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.iotabuild.campuscircle.realtime.dispatcher.SocketEventDispatcher
import com.iotabuild.campuscircle.realtime.model.*
import com.iotabuild.campuscircle.realtime.session.SocketSessionManager
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class RealtimeWebSocketHandler(

    private val sessionManager: SocketSessionManager,

    private val dispatcher: SocketEventDispatcher,

    private val json: ObjectMapper

) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(
        session: WebSocketSession
    ) {

        val uid = session.attributes["uid"] as? String
            ?: run {
                session.close(CloseStatus.NOT_ACCEPTABLE)
                return
            }


        sessionManager.addSession(
            uid,
            session
        )
    }

    override fun handleTextMessage(

        session: WebSocketSession,

        message: TextMessage

    ) {

        val event =
            json.readValue(
                message.payload,
                SocketEvent::class.java
            )

        if (event.eventType == SocketEventType.PING) {
            val pongEvent = SocketEvent(
                eventType = SocketEventType.PONG,
                payload = EmptyPayload()
            )
            session.sendMessage(TextMessage(json.writeValueAsString(pongEvent)))
            return
        }

        dispatcher.dispatch(
            session,
            event
        )
    }

    override fun afterConnectionClosed(

        session: WebSocketSession,

        status: CloseStatus

    ) {

        sessionManager.removeSession(
            session
        )
    }

    override fun handleTransportError(

        session: WebSocketSession,

        exception: Throwable

    ) {

        sessionManager.removeSession(
            session
        )
    }
}