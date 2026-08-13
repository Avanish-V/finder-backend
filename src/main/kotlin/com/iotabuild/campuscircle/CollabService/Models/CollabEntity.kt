package com.iotabuild.campuscircle.CollabService.Models

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "collabs")
class CollabEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false, length = 2000)
    var description: String = "",

    @Column(nullable = false)
    var collabType: String = "", // e.g., Hackathon, Startup

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: UserEntity = UserEntity(),

//    @ElementCollection
//    @CollectionTable(name = "collab_tags", joinColumns = [JoinColumn(name = "collab_id")])
    @Column(name = "requirements")
    var requirements: MutableList<String> = mutableListOf(),

    @Column(name = "participants_needed")
    var participantsNeeded: Int = 1,

    @Column(name = "deadline")
    var deadline: Long = 0,

    @Column(name = "project_url")
    var projectUrl: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

) 

data class CreateCollabRequest(
    val title: String,
    val description: String,
    val collabType: String,
    val requirements: List<String>,
    val participantsNeeded: Int,
    val deadline: Long? = null,
    val projectUrl: String? = null
)

data class CollabResponse(
    val id: String,
    val title: String,
    val description: String,
    val collabType: String,
    val authorName: String,
    val authorId: String,
    val isCurrentUser: Boolean,
    val authorImage: String?,
    val requirements: List<String>,
    val timeAgo: String,
    val participantsNeeded: Int,
    val deadline: Long,
    val projectUrl: String? = null
)

@Entity
@Table(name = "collab_connect_requests")
class CollabConnectRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collab_id", nullable = false)
    val collab: CollabEntity = CollabEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: UserEntity = UserEntity(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CollabRequestStatus = CollabRequestStatus.PENDING,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
) 

enum class CollabRequestStatus {
    PENDING, ACCEPTED, DECLINED,NOT_REQUESTED
}

data class CollabConnectRequestResponse(
    val id: Long,
    val senderId: String,
    val senderName: String,
    val senderImage: String?,
    val tagline : String?,
    val status: String,
    val createdAt: String
)
