package com.iotabuild.campuscircle.Connections.controller

import com.google.api.Page
import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionRequestResponse
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionResponse
import com.iotabuild.campuscircle.Connections.models.entity.ConnectionRequest
import com.iotabuild.campuscircle.Connections.service.ConnectionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/connections")
class ConnectionController(private val service: ConnectionService) {

    // Send a connection request (viewer is authenticated Firebase UID)
    @PostMapping("/request/{receiverUid}")
    fun sendRequest(
        request: HttpServletRequest,
        @PathVariable receiverUid: String
    ): ResponseEntity<ConnectionRequestResponse> {
        val firebaseUser = request.getAttribute("firebaseUser") as FirebaseToken
        val req = service.sendRequest(firebaseUser.uid, receiverUid)
        return ResponseEntity.ok(req)
    }

    // Accept a pending request
    @PostMapping("/request/{id}/accept")
    fun acceptRequest(
        request: HttpServletRequest,
        @PathVariable id: UUID
    ): ResponseEntity<Any> {
        val firebaseUser = request.getAttribute("firebaseUser") as FirebaseToken
        service.acceptRequest(id, firebaseUser.uid)
        return ResponseEntity.ok(mapOf("status" to "ACCEPTED"))
    }

    // Decline a pending request
    @PostMapping("/request/{id}/decline")
    fun declineRequest(@AuthenticationPrincipal uid: String, @PathVariable id: UUID) {
        service.declineRequest(id, uid)
    }

    // Cancel a sent request
    @DeleteMapping("/request/{id}")
    fun cancelRequest( request: HttpServletRequest, @PathVariable id: UUID): ResponseEntity<ConnectionRequestResponse> {
        val firebaseUser = request.getAttribute("firebaseUser") as FirebaseToken
        val result = service.cancelSentRequest(id, firebaseUser.uid)
        return ResponseEntity.ok(result)

    }
    @DeleteMapping("/{id}")
    fun deleteConnection( request: HttpServletRequest, @PathVariable id: UUID): ResponseEntity<ConnectionRequestResponse> {
        val firebaseUser = request.getAttribute("firebaseUser") as FirebaseToken
        val result = service.deleteConnection(id, firebaseUser.uid)
        return ResponseEntity.ok(result)

    }

    // Get pending requests for the authenticated user
    @GetMapping("/requests/pending")
    fun getPending(request:HttpServletRequest) : ResponseEntity<List<ConnectionResponse>>{
        val firebaseUser = request.getAttribute("firebaseUser") as FirebaseToken
        val result = service.getPendingRequestsForUser(firebaseUser.uid)
        return ResponseEntity.ok(result)
    }

    // Get connection status between viewer and profile
    @GetMapping("/status/{profileUid}")
    fun getStatus(
        request: HttpServletRequest,
        @PathVariable profileUid: String
    ): ResponseEntity<ConnectionRequestResponse> {
        val firebaseUser = request.getAttribute("firebaseUser") as FirebaseToken
        val status = service.getConnectionStatus(viewerUid = firebaseUser.uid, profileUid =  profileUid)
        return ResponseEntity.ok(status)
    }

    // List connections with pagination
    @GetMapping("/{userId}")
    fun listConnections(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ResponseEntity<org.springframework.data.domain.Page<ConnectionResponse>>?> {
        val user = service.listConnectionsForUser(userId,page,size)
        return ResponseEntity.ok(user)
    }

    // Count endpoint
//    @GetMapping("/count")
//    fun countConnections(@AuthenticationPrincipal uid: String): ResponseEntity<Any> =
//        ResponseEntity.ok(mapOf("count" to service.listConnectionsForUser(uid, PageRequest.of(0, 1)).second))
}
