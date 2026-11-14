package com.iotabuild.campuscircle.CollabService.Repository

import com.iotabuild.campuscircle.CollabService.Models.CollabFeedbackEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CollabFeedbackRepository : JpaRepository<CollabFeedbackEntity, UUID> {

    fun findAllByCollabIdOrderByCreatedAtDesc(collabId: UUID): List<CollabFeedbackEntity>

    fun findAllByCollabIdOrderByCreatedAtAsc(collabId: UUID): List<CollabFeedbackEntity>

    fun countByParentFeedbackId(parentFeedbackId: UUID): Int

    fun countByCollabId(collabId: UUID): Int

    fun findAllByParentFeedbackId(parentFeedbackId: UUID): List<CollabFeedbackEntity>

    @Modifying
    @Query("DELETE FROM CollabFeedbackEntity f WHERE f.parentFeedback.id = :parentId")
    fun deleteAllByParentFeedbackId(@Param("parentId") parentId: UUID)
}
