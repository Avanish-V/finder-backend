package com.iotabuild.campuscircle.realtime.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class FirebaseHandshakeInterceptor(

    private val firebaseAuth: FirebaseAuth

) : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {

        var token: String? = null
        val authHeader = request.headers.getFirst("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.removePrefix("Bearer ").trim()
        } else {
            val query = request.uri.query
            if (query != null) {
                val params = query.split("&").associate {
                    val parts = it.split("=")
                    parts[0] to parts.getOrNull(1)
                }
                token = params["token"]
            }
        }

        if (token == null) {
            return false
        }

        val decodedToken = try {

            firebaseAuth.verifyIdToken(token)

        } catch (e: FirebaseAuthException) {

            return false

        }

        attributes["uid"] = decodedToken.uid

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) = Unit
}