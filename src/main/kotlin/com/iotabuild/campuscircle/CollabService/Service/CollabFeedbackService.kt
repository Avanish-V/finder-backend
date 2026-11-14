package com.iotabuild.campuscircle.CollabService.Service


import com.iotabuild.campuscircle.CollabService.Models.CollabFeedbackRequest
import com.iotabuild.campuscircle.CollabService.Models.CollabFeedbackResponse
import java.util.UUID

interface CollabFeedbackService {

    /**
     * Retrieves all feedback and replies for a given collaboration, formatted for finder-mobile.
     */
    fun getFeedbackForCollab(collabId: UUID, currentUserId: String): List<CollabFeedbackResponse>

    /**
     * Adds a new top-level feedback or reply (if parentFeedbackId is supplied in request).
     */
    fun addFeedback(collabId: UUID, request: CollabFeedbackRequest, currentUserId: String): CollabFeedbackResponse

    /**
     * Adds a reply to a specific feedback item.
     */
    fun replyToFeedback(collabId: UUID, feedbackId: UUID, request: CollabFeedbackRequest, currentUserId: String): CollabFeedbackResponse

    /**
     * Toggles like state on a feedback item.
     */


    /**
     * Deletes a feedback item and any child replies if requested by feedback author or collab lead.
     */
    fun deleteFeedback(collabId: UUID, feedbackId: UUID, currentUserId: String)
}
