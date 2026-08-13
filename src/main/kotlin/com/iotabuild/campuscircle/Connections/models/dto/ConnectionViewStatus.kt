package com.iotabuild.campuscircle.Connections.models.dto

enum class ConnectionViewStatus {
    CONNECTED,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    NOT_CONNECTED
}

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
