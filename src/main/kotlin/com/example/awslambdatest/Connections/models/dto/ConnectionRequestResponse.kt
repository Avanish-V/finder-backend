package com.iotabuild.campuscircle.Connections.models.dto

import com.iotabuild.campuscircle.Connections.models.entity.RequestStatus

data class ConnectionRequestResponse(
    val requestStatus: ConnectionViewStatus,
    val id: String?=null
)
