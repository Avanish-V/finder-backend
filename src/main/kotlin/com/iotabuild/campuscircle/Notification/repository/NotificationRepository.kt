package com.iotabuild.campuscircle.Notification.repository

import com.iotabuild.campuscircle.Notification.entity.NotificationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<NotificationEntity, Long> {

    fun findByReceiverUidOrderByCreatedAtDesc(
        receiverUid: String,
        pageable: Pageable
    ): Page<NotificationEntity>

    fun countByReceiverUidAndIsReadFalse(
        receiverUid: String
    ): Long

    fun findByIdAndReceiverUid(
        id: Long,
        receiverUid: String
    ): NotificationEntity?

    @Modifying
    @Query("""
        UPDATE NotificationEntity n
        SET n.isRead = true
        WHERE n.receiverUid = :uid
          AND n.isRead = false
    """)
    fun markAllRead(
        @Param("uid") uid: String
    ): Int
}