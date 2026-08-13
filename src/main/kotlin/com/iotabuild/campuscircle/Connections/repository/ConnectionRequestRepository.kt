package com.iotabuild.campuscircle.Connections.repository


import com.iotabuild.campuscircle.Connections.controller.ConnectionController
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionResponse
import com.iotabuild.campuscircle.Connections.models.entity.ConnectionRequest
import com.iotabuild.campuscircle.Connections.models.entity.RequestStatus
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ConnectionRequestRepository : JpaRepository<ConnectionRequest, UUID> {

    fun findBySenderUidAndReceiverUid(senderUid: String, receiverUid: String): ConnectionRequest?

    fun findAllByReceiverUidAndStatus(receiverUid: String, status: RequestStatus): List<ConnectionRequest>


    @Query("""
    SELECT new com.iotabuild.campuscircle.Connections.models.dto.ConnectionResponse(
       cr.id, s.uid, s.name, s.image, s.tagline
    )
    FROM ConnectionRequest cr
    JOIN cr.sender s
    WHERE cr.receiver.uid = :receiverUid
      AND cr.status = :status
""")
    fun findPendingSenderDetailsByReceiverUid(
        @Param("receiverUid") receiverUid: String,
        @Param("status") status: RequestStatus
    ): List<com.iotabuild.campuscircle.Connections.models.dto.ConnectionResponse>


    @Query("""
    SELECT cr
    FROM ConnectionRequest cr
    WHERE (cr.sender.uid = :userA AND cr.receiver.uid = :userB)
       OR (cr.sender.uid = :userB AND cr.receiver.uid = :userA)
""")
    fun findBetweenUsers(
        @Param("userA") userA: String,
        @Param("userB") userB: String
    ): ConnectionRequest?




}
