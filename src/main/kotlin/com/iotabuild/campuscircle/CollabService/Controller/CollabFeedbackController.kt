package com.iotabuild.campuscircle.CollabService.Controller

import com.google.firebase.auth.FirebaseToken

import com.iotabuild.campuscircle.CollabService.Models.CollabFeedbackRequest
import com.iotabuild.campuscircle.CollabService.Models.CollabFeedbackResponse
import com.iotabuild.campuscircle.CollabService.Service.CollabFeedbackService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/api/v1/collabs/{collabId}/feedback")
class CollabFeedbackController(
    private val collabFeedbackService: CollabFeedbackService
) {

    @GetMapping
    fun getFeedback(
        request: HttpServletRequest,
        @PathVariable collabId: UUID
    ): ResponseEntity<List<CollabFeedbackResponse>> {
        val currentUserId = getAuthenticatedUserId(request)
        val feedbackList = collabFeedbackService.getFeedbackForCollab(collabId, currentUserId)
        return ResponseEntity.ok(feedbackList)
    }

    @PostMapping
    fun addFeedback(
        request: HttpServletRequest,
        @PathVariable collabId: UUID,
        @RequestBody body: CollabFeedbackRequest
    ): ResponseEntity<CollabFeedbackResponse> {
        val currentUserId = getAuthenticatedUserId(request)
        val created = collabFeedbackService.addFeedback(collabId, body, currentUserId)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PostMapping("/{feedbackId}/reply")
    fun replyToFeedback(
        request: HttpServletRequest,
        @PathVariable collabId: UUID,
        @PathVariable feedbackId: UUID,
        @RequestBody body: CollabFeedbackRequest
    ): ResponseEntity<CollabFeedbackResponse> {
        val currentUserId = getAuthenticatedUserId(request)
        val createdReply = collabFeedbackService.replyToFeedback(collabId, feedbackId, body, currentUserId)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReply)
    }

    @DeleteMapping("/{feedbackId}")
    fun deleteFeedback(
        request: HttpServletRequest,
        @PathVariable collabId: UUID,
        @PathVariable feedbackId: UUID
    ): ResponseEntity<Map<String, String>> {
        val currentUserId = getAuthenticatedUserId(request)
        collabFeedbackService.deleteFeedback(collabId, feedbackId, currentUserId)
        return ResponseEntity.ok(mapOf("status" to "DELETED", "feedbackId" to feedbackId.toString()))
    }

    private fun getAuthenticatedUserId(request: HttpServletRequest): String {
        val firebaseToken = request.getAttribute("firebaseUser") as? FirebaseToken
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated")
        return firebaseToken.uid
    }
}
