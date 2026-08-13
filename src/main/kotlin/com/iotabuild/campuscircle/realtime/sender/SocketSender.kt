package com.iotabuild.campuscircle.realtime.sender

import com.iotabuild.campuscircle.realtime.model.SocketEvent

interface SocketSender {

    fun sendToUser(
        uid: String,
        event: SocketEvent
    )

    fun broadcast(
        event: SocketEvent
    )

}