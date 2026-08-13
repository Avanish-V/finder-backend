package com.iotabuild.campuscircle.CollabService.Repository

import com.iotabuild.campuscircle.CollabService.Models.CollabConnectRequest
import com.iotabuild.campuscircle.CollabService.Models.CollabRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CollabConnectRequestRepository : JpaRepository<CollabConnectRequest, Long> {
    fun findAllByCollabIdAndStatusIn(collabId: Long,listOf: List<CollabRequestStatus>): List<CollabConnectRequest>
    fun findByCollabIdAndSenderUid(collabId: Long, senderUid: String): CollabConnectRequest?
}
