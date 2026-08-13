package com.iotabuild.campuscircle.realtime.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class SocketEvent(
    val eventType: SocketEventType,

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "eventType"
    )
    @JsonSubTypes(
        JsonSubTypes.Type(value = ChatMessagePayload::class, name = "CHAT_MESSAGE"),
        JsonSubTypes.Type(value = MessageSentPayload::class, name = "MESSAGE_SENT"),
        JsonSubTypes.Type(value = PresencePayload::class, name = "USER_ONLINE"),
        JsonSubTypes.Type(value = PresencePayload::class, name = "USER_OFFLINE"),
        JsonSubTypes.Type(value = TypingPayload::class, name = "USER_TYPING"),
        JsonSubTypes.Type(value = NotificationPayload::class, name = "NOTIFICATION_CREATED"),
        JsonSubTypes.Type(value = EmptyPayload::class, name = "PING"),
        JsonSubTypes.Type(value = EmptyPayload::class, name = "PONG")
    )
    val payload: SocketPayload
)