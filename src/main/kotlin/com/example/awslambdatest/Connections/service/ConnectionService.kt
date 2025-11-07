package com.iotabuild.campuscircle.Connections.service

import com.iotabuild.campuscircle.Connections.controller.ConnectionController
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionRequestResponse
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionResponse
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionViewStatus
import com.iotabuild.campuscircle.Connections.models.entity.Connection
import com.iotabuild.campuscircle.Connections.models.entity.ConnectionRequest
import com.iotabuild.campuscircle.Connections.models.entity.RequestStatus
import com.iotabuild.campuscircle.Connections.repository.ConnectionRepository
import com.iotabuild.campuscircle.Connections.repository.ConnectionRequestRepository
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ConnectionService(
    private val userRepo: UserRepository,
    private val requestRepo: ConnectionRequestRepository,
    private val connectionRepo: ConnectionRepository
) {

    @Transactional
    fun sendRequest(senderUid: String, receiverUid: String): ConnectionRequestResponse {

        if (senderUid == receiverUid) throw IllegalArgumentException("Cannot send request to yourself")

        // prevent creating a request if already connected

        val exist = connectionRepo.existsBetween(senderUid, receiverUid)
        if (exist != null) {
            throw IllegalStateException("User already connected")
        }

        // check existing cross request
//        requestRepo.findBetweenUsers(senderUid, receiverUid)?.let { existing ->
//            if (existing.status == RequestStatus.PENDING) {
//                // if existing sender is someone else, optionally auto-accept? Here we keep it simple.
//                if (existing.senderUid == senderUid) throw IllegalStateException("Request already sent")
//                // If they had sent a request to us, we could choose to accept automatically
//                // For safety, return existing so client can show accept option
//                return ConnectionRequestResponse(
//                    requestStatus = TODO(),
//                )
//            } else {
//                // DECLINED case -> allow new request by inserting a fresh one (or update)
//            }
//        }+

        // ensure users exist
        val sender = userRepo.findById(senderUid).orElseThrow { IllegalStateException("Sender user not found") }
        val receiver = userRepo.findById(receiverUid).orElseThrow { IllegalStateException("Receiver user not found") }

        // create request
        val request = ConnectionRequest(sender = sender, receiver = receiver)
        requestRepo.save(request)
        return ConnectionRequestResponse(requestStatus = ConnectionViewStatus.REQUEST_SENT, id = request.id.toString())
    }

    @Transactional
    fun acceptRequest(requestId: UUID, actorUid: String) {

        val req = requestRepo.findById(requestId).orElseThrow { IllegalArgumentException("Request not found") }

        if (req.receiver.uid != actorUid) throw IllegalStateException("Only receiver can accept")

        // check still pending
        if (req.status != RequestStatus.PENDING) throw IllegalStateException("Request no longer pending")

        // remove request and create a symmetric connection (enforce user_a < user_b)
        requestRepo.delete(req)

        val (a, b) = orderedPair(req.sender.uid, req.receiver.uid)
        // avoid duplicate connection (race)
        if (connectionRepo.existsBetween(a, b) == null) {
            connectionRepo.save(Connection(userA = req.sender, userB = req.receiver, connectedAt = Instant.now()))
        }
    }

    @Transactional
    fun declineRequest(requestId: UUID, actorUid: String) {
        val req = requestRepo.findById(requestId).orElseThrow { IllegalArgumentException("Request not found") }
        if (req.sender.uid != actorUid) throw IllegalStateException("Only receiver can decline")
        if (req.status != RequestStatus.PENDING) throw IllegalStateException("Request not pending")

        req.status = RequestStatus.DECLINED
        req.updatedAt = Instant.now()
        requestRepo.save(req)
    }

    @Transactional
    fun cancelSentRequest(requestId: UUID, actorUid: String): ConnectionRequestResponse {
        val req = requestRepo.findById(requestId).orElseThrow { IllegalArgumentException("Request not found") }
        // allow cancel for pending requests
        if (req.status != RequestStatus.PENDING) throw IllegalStateException("Only pending requests can be cancelled")
        requestRepo.delete(req)
        return ConnectionRequestResponse(requestStatus = ConnectionViewStatus.NOT_CONNECTED)
    }

    @Transactional
    fun deleteConnection(requestId: UUID, actorUid: String): ConnectionRequestResponse {
        val req = connectionRepo.findById(requestId).orElseThrow { IllegalArgumentException("connection not found") }
        connectionRepo.delete(req)
        return ConnectionRequestResponse(requestStatus = ConnectionViewStatus.NOT_CONNECTED)
    }

    @Transactional(readOnly = true)
    fun getPendingRequestsForUser(receiverUid: String): List<ConnectionResponse> =
        requestRepo.findPendingSenderDetailsByReceiverUid(receiverUid, RequestStatus.PENDING)

    @Transactional(readOnly = true)
    fun getConnectionStatus(viewerUid: String, profileUid: String): ConnectionRequestResponse {

        val exist = connectionRepo.existsBetween(viewerUid, profileUid)

        if ( exist!= null) {
            return ConnectionRequestResponse(ConnectionViewStatus.CONNECTED, id = exist.id.toString())
        }

        val req = requestRepo.findBetweenUsers(viewerUid, profileUid)
        return when {
            req == null -> ConnectionRequestResponse(ConnectionViewStatus.NOT_CONNECTED)
            req.status == RequestStatus.PENDING && req.sender.uid == viewerUid ->
                ConnectionRequestResponse(ConnectionViewStatus.REQUEST_SENT, req.id.toString())
            req.status == RequestStatus.PENDING && req.receiver.uid == viewerUid ->
                ConnectionRequestResponse(ConnectionViewStatus.REQUEST_RECEIVED, req.id.toString())
            else -> ConnectionRequestResponse(ConnectionViewStatus.NOT_CONNECTED)
        }
    }


    @Transactional(readOnly = true)
    fun listConnectionsForUser(uid: String,page: Int,size: Int): ResponseEntity<Page<ConnectionResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by("connectedAt").descending())
        val page = connectionRepo.findAllForUser(uid,pageable)
        return ResponseEntity.ok(page)
    }

    private fun orderedPair(a: String, b: String): Pair<String, String> {
        // preserve alphabetical order to satisfy (user_a < user_b) constraint
        return if (a < b) Pair(a, b) else Pair(b, a)
    }


}

