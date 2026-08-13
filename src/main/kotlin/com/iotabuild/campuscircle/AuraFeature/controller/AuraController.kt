package com.iotabuild.campuscircle.AuraFeature.controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.AuraFeature.dto.AuraCheckInResponse
import com.iotabuild.campuscircle.AuraFeature.service.AuraService
import com.iotabuild.campuscircle.UserService.Models.DTOs.AuraInfo
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/me/aura")
class AuraController(private val auraService: AuraService) {

    /**
     * POST /users/me/aura/award
     *
     * Awards the default aura point amount to the caller.
     * Call whenever a meaningful user action occurs (app open, post, etc.).
     *
     * HTTP 200 — updated [AuraCheckInResponse]
     * HTTP 401 — unauthenticated
     */
    @PostMapping("/award")
    fun award(request: HttpServletRequest): ResponseEntity<AuraCheckInResponse> {
        val uid = (request.getAttribute("firebaseUser") as FirebaseToken).uid
        return ResponseEntity.ok(auraService.awardPoints(uid))
    }

    /**
     * GET /users/me/aura
     *
     * Returns the caller's current aura state (points + level).
     * No side effects — safe to poll on app open.
     *
     * HTTP 200 — [AuraInfo]
     * HTTP 401 — unauthenticated
     */
    @GetMapping
    fun getAura(request: HttpServletRequest): ResponseEntity<AuraInfo> {
        val uid = (request.getAttribute("firebaseUser") as FirebaseToken).uid
        return ResponseEntity.ok(auraService.getAuraInfo(uid))
    }
}
