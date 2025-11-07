package com.iotabuild.campuscircle.Connections.models.entity

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

enum class RequestStatus { PENDING, DECLINED }

@Entity
@Table(
    name = "connection_requests",
    uniqueConstraints = [UniqueConstraint(columnNames = ["sender_uid", "receiver_uid"])]
)
data class ConnectionRequest(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_uid", referencedColumnName = "uid")
    val sender: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_uid", referencedColumnName = "uid")
    val receiver: UserEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: RequestStatus = RequestStatus.PENDING,

    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()
)
