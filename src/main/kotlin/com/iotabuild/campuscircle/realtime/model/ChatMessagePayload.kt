package com.iotabuild.campuscircle.realtime.model

data class ChatMessagePayload(

    val roomId: String,

    val recipientId: String,

    val text: String?,

    val attachmentUrl: String?

) : SocketPayload