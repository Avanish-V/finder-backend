package com.iotabuild.campuscircle.SwipeMatch.Service

import com.iotabuild.campuscircle.CollabService.Repository.CollabRepository
import com.iotabuild.campuscircle.CollabService.Service.CollabService
import com.iotabuild.campuscircle.Connections.service.ConnectionService
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeActionRequest
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeActionResult
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeCardDto
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeCardType
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeMatchFeedResponse
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import com.iotabuild.campuscircle.UserService.Service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.abs

@Service
class SwipeMatchService(
    private val collabRepository: CollabRepository,
    private val collabService: CollabService,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val connectionService: ConnectionService
) {

    fun getSwipeFeed(currentUserId: String, page: Int = 0, size: Int = 50): SwipeMatchFeedResponse {
        // 1. Fetch Collaborations
//        val collabsPage = collabService.getCollabs("All", page, size, currentUserId)
//        val collabCards = collabsPage.content
//            .filter { it.authorId != currentUserId }
//            .map { collab ->
//            val matchScore = calculateMatchPercentage(currentUserId, collab.id, collab.requirements)
//            SwipeCardDto(
//                id = collab.id,
//                type = SwipeCardType.COLLABORATION,
//                matchPercentage = matchScore,
//                collab = collab,
//                profile = null
//            )
//        }

        // 2. Fetch User Profiles (excluding current user, and only if completion score > 60)
        val allUsers = userRepository.findAll()
        val profileCards = allUsers
            .filter { it.uid != currentUserId }
            .mapNotNull { user ->
                userService.getUserById(user.uid, currentUserId)?.let { profile ->
                    val completionScore = calculateProfileCompletionScore(profile)
                    if (completionScore > 60) {
                        val skillNames = profile.skills.map { it.name }
                        val matchScore = calculateMatchPercentage(currentUserId, user.uid, skillNames)
                        SwipeCardDto(
                            id = user.uid,
                            type = SwipeCardType.PROFILE,
                            matchPercentage = matchScore,
                            collab = null,
                            profile = profile
                        )
                    } else {
                        null
                    }
                }
            }

        // 3. Interleave collaborations and profiles
        val interleaved = mutableListOf<SwipeCardDto>()
        //val maxLen = maxOf(collabCards.size, profileCards.size)
//        for (i in 0 until maxLen) {
//            if (i < collabCards.size) interleaved.add(collabCards[i])
//            if (i < profileCards.size) interleaved.add(profileCards[i])
//        }

        return SwipeMatchFeedResponse(
            cards = profileCards,
            totalCollabs = 0,
            totalProfiles = profileCards.size
        )
    }

    @Transactional
    fun performSwipeAction(currentUserId: String, request: SwipeActionRequest): SwipeActionResult {
        return try {
            when (request.type) {
                SwipeCardType.COLLABORATION -> {
                    if (request.action.equals("CONNECT", ignoreCase = true) ||
                        request.action.equals("SUPER_LIKE", ignoreCase = true)
                    ) {
                        val collabId = runCatching { UUID.fromString(request.cardId) }.getOrNull()
                            ?: return SwipeActionResult(false, "Invalid collab UUID")
                        val status = collabService.connectToCollab(collabId, currentUserId)
                        SwipeActionResult(true, "Collaboration connect request sent", status.name)
                    } else {
                        SwipeActionResult(true, "Passed collaboration")
                    }
                }
                SwipeCardType.PROFILE -> {
                    if (request.action.equals("CONNECT", ignoreCase = true) ||
                        request.action.equals("SUPER_LIKE", ignoreCase = true)
                    ) {
                        val targetUid = request.cardId
                        val connectionResponse = connectionService.sendRequest(currentUserId, targetUid, request.note)
                        SwipeActionResult(
                            true,
                            if (!request.note.isNullOrBlank()) "Note & connection request sent to profile" else "Connection request sent to profile",
                            connectionResponse.requestStatus.name
                        )
                    } else {
                        SwipeActionResult(true, "Passed profile")
                    }
                }
            }
        } catch (e: Exception) {
            SwipeActionResult(false, e.message ?: "Failed to perform swipe action")
        }
    }

    private fun calculateProfileCompletionScore(profile: com.iotabuild.campuscircle.UserService.Models.DTOs.UserProfileResponse): Int {
        var score = 0
        if (!profile.baseProfile.image.isNullOrBlank()) score += 15
        if (profile.baseProfile.tagline.isNotBlank()) score += 15
        if (profile.baseProfile.summary.isNotBlank()) score += 15
        if (profile.education != null && (profile.education.college.isNotBlank() || profile.education.course.isNotBlank())) score += 20
        if (profile.skills.isNotEmpty()) score += 20
        if (profile.matchPreferences.isNotEmpty()) score += 15
        return score.coerceIn(0, 100)
    }

    private fun calculateMatchPercentage(userId: String, itemId: String, tags: List<String>): Int {
        val baseSeed = abs((userId + itemId).hashCode())
        val base = 75 + (baseSeed % 21) // 75 to 95
        val bonus = if (tags.isNotEmpty()) minOf(tags.size * 2, 4) else 0
        return minOf(base + bonus, 99)
    }
}
