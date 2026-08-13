package com.iotabuild.campuscircle.realtime.model

data class PresencePayload(

    val roomId: String,

    val recipientId: String,

    val active: Boolean

) : SocketPayload