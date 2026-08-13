package com.iotabuild.campuscircle.realtime.mapper


import com.iotabuild.campuscircle.realtime.model.ChatMessagePayload
import com.iotabuild.campuscircle.realtime.model.SocketEvent

interface PayloadMapper {
    fun chatSendMessage(
        event: SocketEvent
    ): ChatMessagePayload
}