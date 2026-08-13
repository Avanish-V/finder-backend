package com.iotabuild.campuscircle.realtime.sender

import com.fasterxml.jackson.databind.ObjectMapper
import com.iotabuild.campuscircle.realtime.model.SocketEvent
import com.iotabuild.campuscircle.realtime.session.SocketSessionManager
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage

@Component
class SocketSenderImpl(

    private val sessionManager: SocketSessionManager,

    private val objectMapper: ObjectMapper

) : SocketSender {

    override fun sendToUser(

        uid: String,

        event: SocketEvent

    ) {

        val session = sessionManager.getSession(uid)

        if (session == null || !session.isOpen) {
            return
        }

        try {

            val json =
                objectMapper.writeValueAsString(event)

            // WebSocketSession.sendMessage() is NOT thread-safe.
            // Synchronize on the session so concurrent sends (e.g. chat + notification)
            // don't cause "IllegalStateException: The output stream is already in use".
            synchronized(session) {
                if (session.isOpen) {
                    session.sendMessage(TextMessage(json))
                }
            }

        } catch (e: Exception) {

            sessionManager.removeSession(session)

        }

     }

     override fun broadcast(
         event: SocketEvent
     ) {

        sessionManager.connectedUsers()
            .forEach {

                sendToUser(
                    it,
                    event
                )

            }

    }

}