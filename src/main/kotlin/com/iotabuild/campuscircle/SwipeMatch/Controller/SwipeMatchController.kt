package com.iotabuild.campuscircle.SwipeMatch.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeActionRequest
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeActionResult
import com.iotabuild.campuscircle.SwipeMatch.Models.SwipeMatchFeedResponse
import com.iotabuild.campuscircle.SwipeMatch.Service.SwipeMatchService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/api/v1/explore/swipe-match")
class SwipeMatchController(
    private val swipeMatchService: SwipeMatchService
) {

    @GetMapping("/cards")
    fun getSwipeCards(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<SwipeMatchFeedResponse> {
        val firebaseToken = request.getAttribute("firebaseUser") as? FirebaseToken
        val currentUserId = firebaseToken?.uid ?: ""
        val response = swipeMatchService.getSwipeFeed(currentUserId, page, size)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/action")
    fun performAction(
        request: HttpServletRequest,
        @RequestBody actionRequest: SwipeActionRequest
    ): ResponseEntity<SwipeActionResult> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val currentUserId = firebaseToken.uid
        val result = swipeMatchService.performSwipeAction(currentUserId, actionRequest)
        return ResponseEntity.ok(result)
    }
}
