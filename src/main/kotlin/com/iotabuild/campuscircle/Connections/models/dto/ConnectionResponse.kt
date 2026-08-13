package com.iotabuild.campuscircle.Connections.models.dto

import java.util.UUID

data class ConnectionResponse(
    val requestId: UUID,
    val uid: String,
    val name: String,
    val image: String?,
    val tagline: String?
)