package com.iotabuild.campuscircle.UserService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.UserService.Models.DTOs.DeviceTokenResponse
import com.iotabuild.campuscircle.UserService.Models.DTOs.RegisterDeviceTokenRequest
import com.iotabuild.campuscircle.UserService.Service.DeviceTokenService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for managing FCM device tokens.
 *
 * All endpoints require a valid Firebase Bearer token.
 *
 * Typical client lifecycle:
 *  1. On login / app launch   → PUT    /api/v1/users/me/device-tokens
 *  2. On logout (all devices) → DELETE /api/v1/users/me/device-tokens
 *  3. On logout (this device) → DELETE /api/v1/users/me/device-tokens/{fcmToken}
 */
@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("api/v1/users/me/device-tokens")
class DeviceTokenController(
    private val deviceTokenService: DeviceTokenService
) {

    // ─── Register / Refresh ──────────────────────────────────────────────────

    @PutMapping
    fun registerToken(
        @RequestBody request: RegisterDeviceTokenRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<DeviceTokenResponse> {
        require(request.fcmToken.isNotBlank()) { "fcmToken cannot be blank" }
        val uid = extractUid(servletRequest)
        val saved = deviceTokenService.registerToken(uid, request)
        return ResponseEntity.status(HttpStatus.OK).body(saved)
    }

    // ─── Read active tokens ───────────────────────────────────────────────────


    @GetMapping
    fun getMyActiveTokens(
        servletRequest: HttpServletRequest
    ): ResponseEntity<List<DeviceTokenResponse>> {
        val uid = extractUid(servletRequest)
        return ResponseEntity.ok(deviceTokenService.getActiveTokens(uid))
    }

    // ─── Logout — all devices ─────────────────────────────────────────────────


    @DeleteMapping
    fun deactivateAll(
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val uid = extractUid(servletRequest)
        deviceTokenService.deactivateAllTokens(uid)
        return ResponseEntity.ok(mapOf("message" to "All device tokens deactivated"))
    }

    // ─── Logout — single device ───────────────────────────────────────────────


    @DeleteMapping("/{fcmToken}")
    fun deactivateSingle(
        @PathVariable fcmToken: String,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val uid = extractUid(servletRequest)
        // Ownership check — ensure the requester owns this token
        val activeTokens = deviceTokenService.getActiveTokens(uid)
        if (activeTokens.none { it.fcmToken == fcmToken }) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("error" to "Token does not belong to the authenticated user"))
        }
        deviceTokenService.deactivateToken(fcmToken)
        return ResponseEntity.ok(mapOf("message" to "Device token deactivated"))
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private fun extractUid(request: HttpServletRequest): String {
        val firebaseToken = request.getAttribute("firebaseUser") as? FirebaseToken
            ?: throw IllegalStateException("No Firebase token found in request")
        return firebaseToken.uid
    }
}
