package com.iotabuild.campuscircle.AuraFeature.service

import com.iotabuild.campuscircle.AuraFeature.dto.AuraCheckInResponse
import com.iotabuild.campuscircle.UserService.Models.DTOs.AuraInfo
import com.iotabuild.campuscircle.UserService.Models.DTOs.AuraLevel
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuraService(
    private val userRepository: UserRepository,
    private val userQueryService: UserQueryService
) {

    companion object {
        /** Flat points awarded for every daily aura top-up. */
        const val POINTS_PER_TOP_UP = 10
    }

    /**
     * Awards [POINTS_PER_TOP_UP] aura points to [uid].
     *
     * The client may call this as many times as desired — each call simply
     * adds points. There is no streak tracking, no daily cap, and no
     * idempotency restriction. Use it whenever the user performs a
     * meaningful action (app open, post, etc.).
     */
    @Transactional
    fun awardPoints(uid: String, points: Int = POINTS_PER_TOP_UP): AuraCheckInResponse {
        val user = userQueryService.getUserOrThrow(uid)
        user.auraPoints = user.auraPoints + points
        userRepository.save(user)

        val auraInfo = buildAuraInfo(user.auraPoints)
        return AuraCheckInResponse(aura = auraInfo, pointsEarnedToday = points)
    }

    /**
     * Returns the current [AuraInfo] for [uid] — read-only, no side effects.
     */
    @Transactional(readOnly = true)
    fun getAuraInfo(uid: String): AuraInfo {
        val user = userQueryService.getUserOrThrow(uid)
        return buildAuraInfo(user.auraPoints)
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private fun buildAuraInfo(points: Int) = AuraInfo(
        auraPoints = points,
        level      = AuraLevel.fromPoints(points)
    )
}
