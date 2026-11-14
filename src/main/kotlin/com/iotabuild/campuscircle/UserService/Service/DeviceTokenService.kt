package com.iotabuild.campuscircle.UserService.Service

import com.iotabuild.campuscircle.UserService.Models.DTOs.DeviceTokenResponse
import com.iotabuild.campuscircle.UserService.Models.DTOs.RegisterDeviceTokenRequest
import com.iotabuild.campuscircle.UserService.Models.Entity.DeviceToken
import com.iotabuild.campuscircle.UserService.Repository.DeviceTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class DeviceTokenService(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val userQueryService: UserQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    // ═══════════════════════════════════════════════════════════════════════
    // Register / Refresh  (call on every app launch after Firebase auth)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Upsert strategy:
     *  1. If this exact FCM token already exists → refresh [lastSeenAt] and
     *     mark it active (handles re-login on the same install).
     *  2. Else if a [deviceId] is provided → replace the previous token for
     *     this device (token rotates after reinstall / token refresh).
     *  3. Else → insert a new row (new device or no device-id provided).
     *
     * Active tokens belonging to the same user on *other* devices are left
     * untouched so push notifications reach all user devices.
     */
    @Transactional
    fun registerToken(uid: String, request: RegisterDeviceTokenRequest): DeviceTokenResponse {
        val user = userQueryService.getUserOrThrow(uid)

        // Case 1 — same token already stored → just refresh it
        val existing = deviceTokenRepository.findByFcmToken(request.fcmToken)
        if (existing != null) {
            existing.isActive   = true
            existing.lastSeenAt = Instant.now()
            existing.appVersion = request.appVersion ?: existing.appVersion
            log.debug("FCM token refreshed for uid={}", uid)
            return DeviceTokenResponse.from(deviceTokenRepository.save(existing))
        }

        // Case 2 — same physical device but token rotated → replace old row
        if (!request.deviceId.isNullOrBlank()) {
            val byDevice = deviceTokenRepository
                .findAllByUserUid(uid)
                .firstOrNull { it.deviceId == request.deviceId }

            if (byDevice != null) {
                byDevice.fcmToken   = request.fcmToken
                byDevice.isActive   = true
                byDevice.lastSeenAt = Instant.now()
                byDevice.appVersion = request.appVersion ?: byDevice.appVersion
                log.debug("FCM token rotated for uid={} deviceId={}", uid, request.deviceId)
                return DeviceTokenResponse.from(deviceTokenRepository.save(byDevice))
            }
        }

        // Case 3 — brand new device / installation
        val newToken = DeviceToken(
            user       = user,
            fcmToken   = request.fcmToken,
            deviceId   = request.deviceId,
            appVersion = request.appVersion,
            isActive   = true,
            lastSeenAt = Instant.now()
        )
        log.debug("FCM token registered for uid={} platform={}", uid)
        return DeviceTokenResponse.from(deviceTokenRepository.save(newToken))
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Logout  (deactivate — do NOT delete so we keep audit history)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Marks **all** tokens for this user as inactive.
     * Call this when the user explicitly logs out so they stop receiving
     * push notifications on every device they own.
     */
    @Transactional
    fun deactivateAllTokens(uid: String) {
        val count = deviceTokenRepository.deactivateAllByUserUid(uid)
        log.info("Deactivated {} FCM tokens for uid={}", count, uid)
    }

    /**
     * Marks only the given FCM token as inactive.
     * Use when the user logs out from a single specific device.
     */
    @Transactional
    fun deactivateToken(fcmToken: String) {
        val entity = deviceTokenRepository.findByFcmToken(fcmToken) ?: return
        entity.isActive = false
        deviceTokenRepository.save(entity)
        log.info("Deactivated single FCM token")
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Read
    // ═══════════════════════════════════════════════════════════════════════

    /** Returns all *active* tokens for a user (used when sending push notifications). */
    @Transactional(readOnly = true)
    fun getActiveTokens(uid: String): List<DeviceTokenResponse> =
        deviceTokenRepository
            .findAllByUserUidAndIsActiveTrue(uid)
            .map { DeviceTokenResponse.from(it) }

    // ═══════════════════════════════════════════════════════════════════════
    // Internal helpers (called by FirebasePushNotificationService)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Called when Firebase returns UNREGISTERED for a token.
     * Removes the stale token permanently since it can never be used again.
     */
    @Transactional
    fun handleInvalidToken(fcmToken: String) {
        val deleted = deviceTokenRepository.deleteByFcmToken(fcmToken)
        if (deleted > 0) {
            log.warn("Deleted invalid/unregistered FCM token")
        }
    }
}
