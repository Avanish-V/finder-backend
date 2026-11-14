package com.iotabuild.campuscircle.SwipeMatch.Models

import com.iotabuild.campuscircle.CollabService.Models.CollabResponse
import com.iotabuild.campuscircle.UserService.Models.DTOs.UserProfileResponse

enum class SwipeCardType {
    COLLABORATION,
    PROFILE
}

data class SwipeCardDto(
    val id: String,
    val type: SwipeCardType,
    val matchPercentage: Int,
    val collab: CollabResponse? = null,
    val profile: UserProfileResponse? = null
)

data class SwipeMatchFeedResponse(
    val cards: List<SwipeCardDto> = emptyList(),
    val totalCollabs: Int = 0,
    val totalProfiles: Int = 0
)

data class SwipeActionRequest(
    val cardId: String,
    val type: SwipeCardType,
    val action: String, // "CONNECT", "PASS", "SUPER_LIKE"
    val note: String? = null
)

data class SwipeActionResult(
    val success: Boolean,
    val message: String,
    val status: String? = null
)
