package com.iotabuild.campuscircle.realtime.config

import com.iotabuild.campuscircle.realtime.auth.FirebaseHandshakeInterceptor
import com.iotabuild.campuscircle.realtime.handler.RealtimeWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(

    private val realtimeWebSocketHandler: RealtimeWebSocketHandler,

    private val firebaseHandshakeInterceptor: FirebaseHandshakeInterceptor

) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(
        registry: WebSocketHandlerRegistry
    ) {

        registry.addHandler(
            realtimeWebSocketHandler,
            "/ws/realtime"
        )
            .addInterceptors(firebaseHandshakeInterceptor)
            .setAllowedOrigins("*")
    }
}