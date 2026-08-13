package com.iotabuild.campuscircle.realtime.model

data class TypingPayload(

    val roomId: String,

    val recipientId: String,

    val typing: Boolean

) : SocketPayload