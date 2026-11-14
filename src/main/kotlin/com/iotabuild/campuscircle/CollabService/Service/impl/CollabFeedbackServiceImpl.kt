package com.iotabuild.campuscircle.CollabService.Service.impl

import com.iotabuild.campuscircle.CollabService.Models.*
import com.iotabuild.campuscircle.CollabService.Repository.CollabFeedbackRepository
import com.iotabuild.campuscircle.CollabService.Repository.CollabRepository
import com.iotabuild.campuscircle.CollabService.Service.CollabFeedbackService
import com.iotabuild.campuscircle.Notification.event.CollabFeedbackEvent
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class CollabFeedbackServiceImpl(
    private val collabFeedbackRepository: CollabFeedbackRepository,
    private val collabRepository: CollabRepository,
    private val userRepository: UserRepository,
    private val publisher: ApplicationEventPublisher
) : CollabFeedbackService {

    @Transactional(readOnly = true)
    override fun getFeedbackForCollab(collabId: UUID, currentUserId: String): List<CollabFeedbackResponse> {
        val collab = collabRepository.findById(collabId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Collab not found with id $collabId")
        }

        val allFeedbacks = collabFeedbackRepository.findAllByCollabIdOrderByCreatedAtAsc(collabId)
        if (allFeedbacks.isEmpty()) return emptyList()

        val allIds = allFeedbacks.mapNotNull { it.id }

        val childCountMap = allFeedbacks.filter { it.parentFeedback?.id != null }
            .groupBy { it.parentFeedback!!.id!! }
            .mapValues { it.value.size }

        val responses = allFeedbacks.map { feedback ->
            mapToResponse(
                feedback = feedback,
                collabAuthorUid = collab.author.uid,
                currentUserId = currentUserId,
                childCount = childCountMap[feedback.id] ?: 0
            )
        }

        // Return parent feedback sorted by newest first, followed by replies sorted chronologically
        val parents = responses.filter { it.parentFeedbackId == null }.sortedByDescending { it.createdAt }
        val replies = responses.filter { it.parentFeedbackId != null }.sortedBy { it.createdAt }

        return parents + replies
    }

    override fun addFeedback(
        collabId: UUID,
        request: CollabFeedbackRequest,
        currentUserId: String
    ): CollabFeedbackResponse {
        val content = request.content.trim()
        if (content.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback content cannot be empty")
        }

        val collab = collabRepository.findById(collabId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Collab not found with id $collabId")
        }

        val user = userRepository.findById(currentUserId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id $currentUserId")
        }

        val parentFeedback = request.parentFeedbackId?.let { parentIdStr ->
            val parentUuid = runCatching { UUID.fromString(parentIdStr) }.getOrElse {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parent feedback ID format")
            }
            collabFeedbackRepository.findById(parentUuid).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Parent feedback not found with id $parentUuid")
            }.also { parent ->
                if (parent.collab.id != collabId) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent feedback does not belong to this collab")
                }
            }
        }

        val newFeedback = CollabFeedbackEntity(
            collab = collab,
            author = user,
            parentFeedback = parentFeedback,
            content = content,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val saved = collabFeedbackRepository.save(newFeedback)

        // Publish notification event
        val receiverUid = if (parentFeedback != null) parentFeedback.author.uid else collab.author.uid
        if (receiverUid != currentUserId) {
            publisher.publishEvent(
                CollabFeedbackEvent(
                    receiverUid = receiverUid,
                    senderUid = currentUserId,
                    collabId = collabId,
                    collabTitle = collab.title,
                    feedbackId = saved.id!!,
                    isReply = parentFeedback != null
                )
            )
        }

        return mapToResponse(
            feedback = saved,
            collabAuthorUid = collab.author.uid,
            currentUserId = currentUserId,
            childCount = 0
        )
    }

    override fun replyToFeedback(
        collabId: UUID,
        feedbackId: UUID,
        request: CollabFeedbackRequest,
        currentUserId: String
    ): CollabFeedbackResponse {
        val replyRequest = request.copy(parentFeedbackId = feedbackId.toString())
        return addFeedback(collabId, replyRequest, currentUserId)
    }



    override fun deleteFeedback(collabId: UUID, feedbackId: UUID, currentUserId: String) {
        val feedback = collabFeedbackRepository.findById(feedbackId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found with id $feedbackId")
        }

        if (feedback.collab.id != collabId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback does not belong to specified collab")
        }

        val isFeedbackAuthor = feedback.author.uid == currentUserId
        val isCollabLead = feedback.collab.author.uid == currentUserId

        if (!isFeedbackAuthor && !isCollabLead) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this feedback")
        }

        // Collect child replies if this is a parent feedback
        val childReplies = collabFeedbackRepository.findAllByParentFeedbackId(feedbackId)
        val childIds = childReplies.mapNotNull { it.id }

        // Remove likes for child replies and the feedback itself
        if (childIds.isNotEmpty()) {
            collabFeedbackRepository.deleteAllByParentFeedbackId(feedbackId)
        }

        collabFeedbackRepository.delete(feedback)
    }

    private fun mapToResponse(
        feedback: CollabFeedbackEntity,
        collabAuthorUid: String,
        currentUserId: String,
        childCount: Int
    ): CollabFeedbackResponse {
        val author = feedback.author
        return CollabFeedbackResponse(
            id = feedback.id?.toString() ?: "",
            collabId = feedback.collab.id?.toString() ?: "",
            content = feedback.content,
            author = CollabAuthorDetails(
                authorId = author.uid,
                authorName = author.name.ifBlank { "Student Peer" },
                authorImage = author.image,
                authorTagline = author.tagline,
                isProjectLead = author.uid == collabAuthorUid,
                isCurrentUser = author.uid == currentUserId
            ),
            parentFeedbackId = feedback.parentFeedback?.id?.toString(),
            createdAt = feedback.createdAt.toString(),
            timeAgo = formatTimeAgo(feedback.createdAt),
            childCount = childCount
        )
    }

    private fun formatTimeAgo(dateTime: LocalDateTime): String {
        val duration = Duration.between(dateTime, LocalDateTime.now())
        return when {
            duration.isNegative || duration.toMinutes() < 1 -> "Just now"
            duration.toHours() < 1 -> "${duration.toMinutes()}m ago"
            duration.toDays() < 1 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> "${duration.toDays() / 7}w ago"
        }
    }
}
