package com.iotabuild.campuscircle.realtime.dispatcher

import org.springframework.web.socket.WebSocketSession
import com.iotabuild.campuscircle.realtime.model.SocketEvent

interface SocketEventDispatcher {

    fun dispatch(
        session: WebSocketSession,
        event: SocketEvent
    )
}