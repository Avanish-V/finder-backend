package com.iotabuild.campuscircle.CollabService

import com.iotabuild.campuscircle.CollabService.Models.*
import com.iotabuild.campuscircle.CollabService.Repository.CollabFeedbackRepository
import com.iotabuild.campuscircle.CollabService.Repository.CollabRepository
import com.iotabuild.campuscircle.CollabService.Service.impl.CollabFeedbackServiceImpl
import com.iotabuild.campuscircle.Notification.event.CollabFeedbackEvent
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

class CollabFeedbackServiceTest {

    private lateinit var feedbackRepository: CollabFeedbackRepository
    private lateinit var feedbackLikeRepository: CollabFeedbackLikeRepository
    private lateinit var collabRepository: CollabRepository
    private lateinit var userRepository: UserRepository
    private lateinit var publisher: ApplicationEventPublisher

    private lateinit var service: CollabFeedbackServiceImpl

    private val collabId = UUID.randomUUID()
    private val authorUid = "collab_owner_123"
    private val peerUid = "peer_user_456"

    private lateinit var collabAuthor: UserEntity
    private lateinit var peerUser: UserEntity
    private lateinit var collabEntity: CollabEntity

    @BeforeEach
    fun setUp() {
        feedbackRepository = mock(CollabFeedbackRepository::class.java)
        feedbackLikeRepository = mock(CollabFeedbackLikeRepository::class.java)
        collabRepository = mock(CollabRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        publisher = mock(ApplicationEventPublisher::class.java)

        service = CollabFeedbackServiceImpl(
            feedbackRepository,
            feedbackLikeRepository,
            collabRepository,
            userRepository,
            publisher
        )

        collabAuthor = UserEntity(
            uid = authorUid,
            name = "Project Lead",
            image = "https://example.com/lead.jpg",
            tagline = "Android Lead"
        )

        peerUser = UserEntity(
            uid = peerUid,
            name = "Fellow Dev",
            image = "https://example.com/peer.jpg",
            tagline = "Backend Dev"
        )

        collabEntity = CollabEntity(
            id = collabId,
            title = "Awesome Campus App",
            description = "Building next-gen app",
            collabType = "Project",
            author = collabAuthor
        )
    }

    @Test
    fun `addFeedback successfully creates top level feedback and publishes notification`() {
        `when`(collabRepository.findById(collabId)).thenReturn(Optional.of(collabEntity))
        `when`(userRepository.findById(peerUid)).thenReturn(Optional.of(peerUser))

        val savedEntity = CollabFeedbackEntity(
            id = UUID.randomUUID(),
            collab = collabEntity,
            author = peerUser,
            parentFeedback = null,
            content = "Great initiative!",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        `when`(feedbackRepository.save(any(CollabFeedbackEntity::class.java))).thenReturn(savedEntity)

        val request = CollabFeedbackRequest(content = "Great initiative!")
        val response = service.addFeedback(collabId, request, peerUid)

        assertNotNull(response.id)
        assertEquals("Great initiative!", response.content)
        assertEquals(peerUid, response.author.authorId)
        assertEquals("Fellow Dev", response.author.authorName)
        assertTrue(response.author.isCurrentUser)
        assertFalse(response.author.isProjectLead)
        assertNull(response.parentFeedbackId)
        assertEquals(0, response.likesCount)
        assertFalse(response.isLiked)

        verify(publisher, times(1)).publishEvent(any(CollabFeedbackEvent::class.java))
    }

    @Test
    fun `addFeedback throws exception when content is blank`() {
        val request = CollabFeedbackRequest(content = "   ")
        assertThrows<ResponseStatusException> {
            service.addFeedback(collabId, request, peerUid)
        }
    }

    @Test
    fun `replyToFeedback successfully creates reply linked to parent feedback`() {
        val parentId = UUID.randomUUID()
        val parentFeedback = CollabFeedbackEntity(
            id = parentId,
            collab = collabEntity,
            author = peerUser,
            content = "Initial feedback",
            createdAt = LocalDateTime.now().minusHours(1)
        )

        `when`(collabRepository.findById(collabId)).thenReturn(Optional.of(collabEntity))
        `when`(userRepository.findById(authorUid)).thenReturn(Optional.of(collabAuthor))
        `when`(feedbackRepository.findById(parentId)).thenReturn(Optional.of(parentFeedback))

        val replyEntity = CollabFeedbackEntity(
            id = UUID.randomUUID(),
            collab = collabEntity,
            author = collabAuthor,
            parentFeedback = parentFeedback,
            content = "Thanks for the feedback!",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        `when`(feedbackRepository.save(any(CollabFeedbackEntity::class.java))).thenReturn(replyEntity)

        val request = CollabFeedbackRequest(content = "Thanks for the feedback!")
        val response = service.replyToFeedback(collabId, parentId, request, authorUid)

        assertEquals("Thanks for the feedback!", response.content)
        assertEquals(parentId.toString(), response.parentFeedbackId)
        assertTrue(response.author.isProjectLead)
        assertTrue(response.author.isCurrentUser)
    }

    @Test
    fun `getFeedbackForCollab returns sorted list with like and reply counts`() {
        val parentId = UUID.randomUUID()
        val parent = CollabFeedbackEntity(
            id = parentId,
            collab = collabEntity,
            author = peerUser,
            content = "Nice work",
            createdAt = LocalDateTime.now().minusHours(2)
        )

        val reply = CollabFeedbackEntity(
            id = UUID.randomUUID(),
            collab = collabEntity,
            author = collabAuthor,
            parentFeedback = parent,
            content = "Thank you!",
            createdAt = LocalDateTime.now().minusHours(1)
        )

        `when`(collabRepository.findById(collabId)).thenReturn(Optional.of(collabEntity))
        `when`(feedbackRepository.findAllByCollabIdOrderByCreatedAtAsc(collabId)).thenReturn(listOf(parent, reply))
        `when`(feedbackLikeRepository.findAllByFeedbackIdIn(anyList())).thenReturn(emptyList())

        val result = service.getFeedbackForCollab(collabId, authorUid)

        assertEquals(2, result.size)
        // Parent should be first
        assertEquals(parentId.toString(), result[0].id)
        assertNull(result[0].parentFeedbackId)
        assertEquals(1, result[0].childCount) // 1 reply to parent

        // Reply is second
        assertEquals(parentId.toString(), result[1].parentFeedbackId)
    }

    @Test
    fun `toggleLike adds like when not liked and removes like when already liked`() {
        val feedbackId = UUID.randomUUID()
        val feedback = CollabFeedbackEntity(
            id = feedbackId,
            collab = collabEntity,
            author = peerUser,
            content = "Feedback"
        )

        `when`(collabRepository.findById(collabId)).thenReturn(Optional.of(collabEntity))
        `when`(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback))
        `when`(userRepository.findById(peerUid)).thenReturn(Optional.of(peerUser))
        `when`(feedbackLikeRepository.findByFeedbackIdAndUserUid(feedbackId, peerUid)).thenReturn(Optional.empty())
        `when`(feedbackLikeRepository.countByFeedbackId(feedbackId)).thenReturn(1)

        val likeResponse = service.toggleLike(collabId, feedbackId, peerUid)
        assertTrue(likeResponse.isLiked)
        assertEquals(1, likeResponse.likesCount)

        // Now toggle again to unlike
        val existingLike = CollabFeedbackLikeEntity(id = UUID.randomUUID(), feedback = feedback, user = peerUser)
        `when`(feedbackLikeRepository.findByFeedbackIdAndUserUid(feedbackId, peerUid)).thenReturn(Optional.of(existingLike))
        `when`(feedbackLikeRepository.countByFeedbackId(feedbackId)).thenReturn(0)

        val unlikeResponse = service.toggleLike(collabId, feedbackId, peerUid)
        assertFalse(unlikeResponse.isLiked)
        assertEquals(0, unlikeResponse.likesCount)
    }

    @Test
    fun `deleteFeedback succeeds for author and fails for unauthorized user`() {
        val feedbackId = UUID.randomUUID()
        val feedback = CollabFeedbackEntity(
            id = feedbackId,
            collab = collabEntity,
            author = peerUser,
            content = "To be deleted"
        )

        `when`(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback))

        // Unauthorized user attempts delete
        val strangerUid = "stranger_789"
        assertThrows<ResponseStatusException> {
            service.deleteFeedback(collabId, feedbackId, strangerUid)
        }

        // Author deletes
        service.deleteFeedback(collabId, feedbackId, peerUid)
        verify(feedbackRepository, times(1)).delete(feedback)
    }
}
