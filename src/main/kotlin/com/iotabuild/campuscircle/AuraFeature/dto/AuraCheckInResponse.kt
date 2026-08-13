package com.iotabuild.campuscircle.AuraFeature.dto

import com.iotabuild.campuscircle.UserService.Models.DTOs.AuraInfo

/**
 * Returned by POST /users/me/aura/award — the updated aura state and
 * how many points were added in this single call.
 */
data class AuraCheckInResponse(
    val aura: AuraInfo,
    val pointsEarnedToday: Int
)
