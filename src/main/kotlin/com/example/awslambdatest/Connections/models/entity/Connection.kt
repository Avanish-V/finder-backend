package com.iotabuild.campuscircle.Connections.models.entity

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "connections",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_a", "user_b"])]
)
data class Connection(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a", referencedColumnName = "uid", nullable = false)
    val userA: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b", referencedColumnName = "uid", nullable = false)
    val userB: UserEntity,

    @Column(name = "connected_at")
    val connectedAt: Instant = Instant.now()
)
