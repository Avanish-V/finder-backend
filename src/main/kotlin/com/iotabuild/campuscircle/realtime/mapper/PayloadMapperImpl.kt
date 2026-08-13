package com.iotabuild.campuscircle.realtime.mapper

import com.iotabuild.campuscircle.realtime.model.ChatMessagePayload
import com.iotabuild.campuscircle.realtime.model.SocketEvent
import org.springframework.stereotype.Component

@Component
class PayloadMapperImpl : PayloadMapper {

    override fun chatSendMessage(
        event: SocketEvent
    ): ChatMessagePayload {
        return event.payload as ChatMessagePayload
    }
}