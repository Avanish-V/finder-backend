package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.iotabuild.campuscircle.UserService.Models.Entity.DevicePlatform
import com.iotabuild.campuscircle.UserService.Models.Entity.DeviceToken
import java.time.Instant

// ─── Request ────────────────────────────────────────────────────────────────


data class RegisterDeviceTokenRequest(
    val fcmToken: String,
    val deviceId: String? = null,
    val appVersion: String? = null
)

// ─── Response ────────────────────────────────────────────────────────────────

data class DeviceTokenResponse(
    val id: Long,
    val fcmToken: String,
    val deviceId: String?,
    val appVersion: String?,
    val isActive: Boolean,
    val lastSeenAt: Instant,
    val createdAt: Instant
) {
    companion object {
        fun from(entity: DeviceToken) = DeviceTokenResponse(
            id         = entity.id!!,
            fcmToken   = entity.fcmToken,
            deviceId   = entity.deviceId,
            appVersion = entity.appVersion,
            isActive   = entity.isActive,
            lastSeenAt = entity.lastSeenAt,
            createdAt  = entity.createdAt
        )
    }
}
