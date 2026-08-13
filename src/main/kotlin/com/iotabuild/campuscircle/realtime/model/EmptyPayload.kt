package com.iotabuild.campuscircle.realtime.model

data class EmptyPayload(
    val value: String = ""
) : SocketPayload
