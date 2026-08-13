package com.iotabuild.campuscircle.realtime.session

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class SocketSessionManagerImpl : SocketSessionManager {

    private val sessions =
        ConcurrentHashMap<String, WebSocketSession>()

    override fun addSession(
        uid: String,
        session: WebSocketSession
    ) {
        // Remove the old session from the map first, then close it outside any lock.
        // Closing inside the ConcurrentHashMap compute path can cause a deadlock if
        // the WebSocket close itself triggers a removeSession callback.
        val old = sessions.put(uid, session)
        try { old?.close() } catch (_: Exception) { }
    }

    override fun removeSession(
        session: WebSocketSession
    ) {

        sessions.entries.removeIf {

            it.value.id == session.id

        }

    }

    override fun getSession(
        uid: String
    ): WebSocketSession? {

        return sessions[uid]
    }

    override fun isOnline(
        uid: String
    ): Boolean {

        return sessions[uid]?.isOpen == true
    }

    override fun connectedUsers(): Set<String> {

        return sessions.keys
    }
}