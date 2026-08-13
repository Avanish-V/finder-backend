package com.iotabuild.campuscircle.realtime.dispatcher

import com.iotabuild.campuscircle.realtime.handler.SocketEventHandler
import com.iotabuild.campuscircle.realtime.model.SocketEvent
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class SocketEventDispatcherImpl(

    handlers: List<SocketEventHandler>

) : SocketEventDispatcher {

    private val handlerMap =
        handlers.associateBy { it.eventType }

    override fun dispatch(
        session: WebSocketSession,
        event: SocketEvent
    ) {

        handlerMap[event.eventType]?.handle(session, event)

    }
}