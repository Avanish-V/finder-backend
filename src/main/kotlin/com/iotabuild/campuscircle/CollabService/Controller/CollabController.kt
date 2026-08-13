package com.iotabuild.campuscircle.CollabService.Controller

import com.iotabuild.campuscircle.CollabService.Models.*
import com.iotabuild.campuscircle.CollabService.Service.CollabService
import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.Connections.models.entity.ConnectionRequest
import jakarta.servlet.http.HttpServletRequest
import org.hibernate.annotations.Parameter
import org.springframework.data.domain.Page
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/collabs")
class CollabController(
    private val collabService: CollabService
) {

    @PostMapping
    fun createCollab(
        request: HttpServletRequest,
        @RequestBody createRequest: CreateCollabRequest
    ): ResponseEntity<CollabResponse> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        val response = collabService.createCollab(createRequest, userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getCollabs(
        request: HttpServletRequest,
        @RequestParam(required = false, defaultValue = "Explore") tab: String,
        @RequestParam(required = false, defaultValue = "All") type: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<CollabResponse>> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        val collabs = collabService.getCollabs(type, page, size, userId)
        return ResponseEntity.ok(collabs)
    }

    @GetMapping("/{id}")
    fun getCollabById(
        request: HttpServletRequest,
        @PathVariable id: Long
    ): ResponseEntity<CollabResponse> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        val response = collabService.getCollabById(id,userId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteCollab(
        request: HttpServletRequest,
        @PathVariable id: Long
    ): ResponseEntity<Map<String, String>> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        collabService.deleteCollab(id, userId)
        return ResponseEntity.ok(mapOf("status" to "DELETED"))
    }

    @PostMapping("/{id}/connect")
    fun connectToCollab(
        request: HttpServletRequest,
        @PathVariable id: Long
    ): ResponseEntity<CollabRequestStatus> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        val response = collabService.connectToCollab(id, userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/requests")
    fun getRequests(
        request: HttpServletRequest,
        @PathVariable id: Long
    ): ResponseEntity<List<CollabConnectRequestResponse>> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        val response = collabService.getRequestsForCollab(id, userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("has-requested")
    fun hasRequested(
        request: HttpServletRequest,
        @RequestParam collabId: Long
    ): ResponseEntity<CollabRequestStatus>{
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        val response = collabService.hasAlreadyApplied(collabId,userId)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/requests/{requestId}")
    fun updateRequestStatus(
        request: HttpServletRequest,
        @PathVariable requestId: Long,
        @RequestParam status: CollabRequestStatus
    ): ResponseEntity<CollabRequestStatus> {
        val firebaseToken = request.getAttribute("firebaseUser") as FirebaseToken
        val userId = firebaseToken.uid
        val response = collabService.updateRequestStatus(requestId, status, userId)
        return ResponseEntity.ok(response)
    }
}
