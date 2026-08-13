package com.iotabuild.campuscircle.realtime.handler

import com.iotabuild.campuscircle.realtime.model.SocketEvent
import com.iotabuild.campuscircle.realtime.model.SocketEventType
import org.springframework.web.socket.WebSocketSession

interface SocketEventHandler {

    val eventType: SocketEventType

    fun handle(
        session: WebSocketSession,
        event: SocketEvent
    )
}