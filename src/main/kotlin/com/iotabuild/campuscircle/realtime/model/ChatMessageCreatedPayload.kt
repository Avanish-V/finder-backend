package com.iotabuild.campuscircle.realtime.model


data class ChatMessageCreatedPayload(

    val messageId: String,

    val roomId: String,

    val senderUid: String,

    val text: String,

    val attachmentUrl: String?,

    val timestamp: Long,

    val read: Boolean

) : SocketPayload