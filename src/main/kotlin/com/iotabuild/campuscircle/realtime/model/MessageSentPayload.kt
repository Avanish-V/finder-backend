package com.iotabuild.campuscircle.realtime.model

data class MessageSentPayload(

    val localId: String,

    val messageId: String

) : SocketPayload