package com.iotabuild.campuscircle.CollabService.Service

import com.iotabuild.campuscircle.CollabService.Models.*
import com.iotabuild.campuscircle.CollabService.Repository.CollabConnectRequestRepository
import com.iotabuild.campuscircle.CollabService.Repository.CollabRepository
import com.iotabuild.campuscircle.CollabService.Exceptions.*
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import java.time.Duration
import java.time.LocalDateTime

@Service
class CollabService(
    private val collabRepository: CollabRepository,
    private val userRepository: UserRepository,
    private val collabConnectRequestRepository: CollabConnectRequestRepository
) {

    fun createCollab(request: CreateCollabRequest, userId: String): CollabResponse {
        val user = userRepository.findById(userId).orElseThrow { 
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") 
        }

        val newCollab = CollabEntity(
            title = request.title,
            description = request.description,
            collabType = request.collabType,
            author = user,
            requirements = request.requirements.toMutableList(),
            participantsNeeded = request.participantsNeeded,
            deadline = request.deadline ?: 0L,
            projectUrl = request.projectUrl
        )

        val savedCollab = collabRepository.save(newCollab)
        return savedCollab.toResponse(userId)
    }

    fun getCollabs(type: String, page: Int, size: Int,userId:String): Page<CollabResponse> {

        val pageable = PageRequest.of(page, size)

        val collabs = if (type.equals("All", ignoreCase = true)) {
            collabRepository.findAllByOrderByCreatedAtDesc(pageable)
        } else {
            collabRepository.findByCollabTypeOrderByCreatedAtDesc(type, pageable)
        }
        
        return collabs.map { it.toResponse(userId) }
    }

    fun getCollabById(collabId: Long,userId: String): CollabResponse {
        val collab = collabRepository.findById(collabId).orElseThrow { 
            CollabNotFoundException("Collab not found") 
        }
        return collab.toResponse(userId)
    }

    fun deleteCollab(collabId: Long, userId: String) {
        val collab = collabRepository.findById(collabId).orElseThrow { 
            CollabNotFoundException("Collab not found") 
        }
        if (collab.author.uid != userId) {
            throw UnauthorizedCollabException("You are not authorized to delete this collab post")
        }
        collabRepository.delete(collab)
    }

    fun connectToCollab(collabId: Long, userId: String): CollabRequestStatus {
        val collab = collabRepository.findById(collabId).orElseThrow {
            CollabNotFoundException("Collab not found") 
        }
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        val existing = collabConnectRequestRepository.findByCollabIdAndSenderUid(collabId, userId)

        if (existing != null) {
            return existing.status
        }

        val request = CollabConnectRequest(
            collab = collab,
            sender = user
        )
        val saved = collabConnectRequestRepository.save(request)
        return saved.status
    }

    fun getRequestsForCollab(collabId: Long, userId: String): List<CollabConnectRequestResponse> {
        val collab = collabRepository.findById(collabId).orElseThrow { 
            CollabNotFoundException("Collab not found") 
        }
        if (collab.author.uid != userId) {
            throw UnauthorizedCollabException("You are not authorized to view requests for this collab")
        }
        return collabConnectRequestRepository.findAllByCollabIdAndStatusIn(collabId,listOf(CollabRequestStatus.ACCEPTED, CollabRequestStatus.PENDING)).map { it.toResponse() }
    }

    fun updateRequestStatus(requestId: Long, status: CollabRequestStatus, userId: String): CollabRequestStatus {
        val request = collabConnectRequestRepository.findById(requestId).orElseThrow { 
            ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found") 
        }
        if (request.collab.author.uid != userId) {
            throw UnauthorizedCollabException("You are not authorized to update this request")
        }
        request.status = status
        val saved = collabConnectRequestRepository.save(request)
        return saved.status
    }

    fun hasAlreadyApplied(collabId: Long,userId: String): CollabRequestStatus{

        val hasRequested = collabConnectRequestRepository.findByCollabIdAndSenderUid(collabId,userId)

        return hasRequested?.status ?: CollabRequestStatus.NOT_REQUESTED
    }









    private fun CollabConnectRequest.toResponse() = CollabConnectRequestResponse(
        id = id,
        senderId = sender.uid,
        senderName = sender.name,
        senderImage = sender.image,
        tagline = sender.tagline,
        status = status.name,
        createdAt = formatTimeAgo(createdAt)
    )

    private fun CollabEntity.toResponse(userId: String) = CollabResponse(
        id = id.toString(),
        title = title,
        description = description,
        collabType = collabType,
        authorName = author.name.ifBlank { "Unknown User" },
        authorId = author.uid,
        isCurrentUser = author.uid == userId,
        authorImage = author.image,
        requirements = requirements.toList(),
        timeAgo = formatTimeAgo(createdAt),
        participantsNeeded = participantsNeeded,
        deadline = deadline,
        projectUrl = projectUrl
    )

    private fun formatTimeAgo(dateTime: LocalDateTime): String {
        val duration = Duration.between(dateTime, LocalDateTime.now())
        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toHours() < 1 -> "${duration.toMinutes()}m ago"
            duration.toDays() < 1 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> "${duration.toDays() / 7}w ago"
        }
    }
}
