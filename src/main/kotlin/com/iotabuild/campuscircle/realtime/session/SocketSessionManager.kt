package com.iotabuild.campuscircle.realtime.session

import org.springframework.web.socket.WebSocketSession

interface SocketSessionManager {

    fun addSession(
        uid: String,
        session: WebSocketSession
    )

    fun removeSession(
        session: WebSocketSession
    )

    fun getSession(
        uid: String
    ): WebSocketSession?

    fun isOnline(
        uid: String
    ): Boolean

    fun connectedUsers(): Set<String>
}