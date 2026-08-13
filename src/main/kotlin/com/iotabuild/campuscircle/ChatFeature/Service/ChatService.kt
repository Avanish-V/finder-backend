package com.iotabuild.campuscircle.ChatFeature.Service

import com.iotabuild.campuscircle.ChatFeature.Models.*
import com.iotabuild.campuscircle.ChatFeature.Repository.ChatMessageRepository
import com.iotabuild.campuscircle.ChatFeature.Repository.ChatParticipantRepository
import com.iotabuild.campuscircle.ChatFeature.Repository.ChatRoomRepository
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@Service
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val userRepository: UserRepository
) {

    private fun LocalDateTime.toEpochMilli(): Long {
        return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @Transactional(readOnly = true)
    fun getChats(userId: String): List<UserChatsDTO> {
        val userParticipants = chatParticipantRepository.findByUserUid(userId)
        val chatList = mutableListOf<UserChatsDTO>()

        for (participant in userParticipants) {
            val room = participant.room
            val allRoomParticipants = chatParticipantRepository.findByRoomId(room.id)
            val recipientParticipant = allRoomParticipants.firstOrNull { it.user.uid != userId } ?: continue

            val lastMessageEntity = chatMessageRepository.findFirstByRoomIdOrderByCreatedAtDesc(room.id)
            val unreadCount = chatMessageRepository.countByRoomIdAndSenderUidNotAndIsReadFalse(room.id, userId)

            val lastMsgText = lastMessageEntity?.text ?: ""
            val lastMsgTime = lastMessageEntity?.createdAt?.toEpochMilli() ?: room.createdAt.toEpochMilli()
            val isRead = lastMessageEntity?.isRead ?: false
            val lastMsgBy = lastMessageEntity?.sender?.uid == userId

            chatList.add(
                UserChatsDTO(
                    roomId = room.id.toString(),
                    receiverId = recipientParticipant.user.uid,
                    userName = recipientParticipant.user.name,
                    userImage = recipientParticipant.user.image,
                    lastMessage = LastMessage(
                        lastMessage = lastMsgText,
                        timeStamp = lastMsgTime,
                        unreadCount = unreadCount,
                        isRead = isRead,
                        lastMessageBy = lastMsgBy
                    )
                )
            )
        }

        return chatList.sortedByDescending { it.lastMessage.timeStamp }
    }

    @Transactional
    fun fetchOrCreatePrivateRoom(senderId: String, recipientId: String): String {
        val existingRoom = chatParticipantRepository.findCommonRoom(senderId, recipientId)
        if (existingRoom != null) {
            return existingRoom.id.toString()
        }

        // Create new Room
        val newRoom = chatRoomRepository.save(ChatRoomEntity())

        val sender = userRepository.findById(senderId)
            .orElseThrow { IllegalArgumentException("Sender not found with ID: $senderId") }
        val recipient = userRepository.findById(recipientId)
            .orElseThrow { IllegalArgumentException("Recipient not found with ID: $recipientId") }

        // Create participants
        chatParticipantRepository.save(ChatParticipantEntity(room = newRoom, user = sender))
        chatParticipantRepository.save(ChatParticipantEntity(room = newRoom, user = recipient))

        return newRoom.id.toString()
    }

    /** Validates that [userId] is a participant in the given room. */
    @Transactional(readOnly = true)
    fun isParticipant(roomIdString: String, userId: String): Boolean {
        val roomId = try {
            UUID.fromString(roomIdString)
        } catch (e: IllegalArgumentException) {
            return false
        }
        return chatParticipantRepository.findByRoomIdAndUserUid(roomId, userId) != null
    }

    /**
     * Returns paginated messages for [roomIdString].
     * [requestingUserId] must be a participant in the room; throws [AccessDeniedException] otherwise.
     */
    @Transactional(readOnly = true)
    fun getMessages(roomIdString: String, requestingUserId: String, page: Int, size: Int): List<ChatMessageDTO> {
        if (!isParticipant(roomIdString, requestingUserId)) {
            throw AccessDeniedException("User is not a participant in room: $roomIdString")
        }
        val roomId = UUID.fromString(roomIdString)
        val pageable = PageRequest.of(page, size)
        val messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)

        return messages.map {
            ChatMessageDTO(
                messageId = it.id.toString(),
                senderId = it.sender.uid,
                text = it.text,
                attachmentUrl = it.attachmentUrl,
                timestamp = it.createdAt.toEpochMilli(),
                read = it.isRead
            )
        }
    }

    @Transactional
    fun saveMessage(roomIdString: String, senderId: String, text: String, attachmentUrl: String?): ChatMessageEntity {
        // Prevent users from posting to rooms they are not a member of
        if (!isParticipant(roomIdString, senderId)) {
            throw AccessDeniedException("User is not a participant in room: $roomIdString")
        }
        val roomId = UUID.fromString(roomIdString)
        val room = chatRoomRepository.findById(roomId)
            .orElseThrow { IllegalArgumentException("Room not found: $roomId") }
        val sender = userRepository.findById(senderId)
            .orElseThrow { IllegalArgumentException("Sender not found: $senderId") }

        val message = ChatMessageEntity(
            room = room,
            sender = sender,
            text = text,
            attachmentUrl = attachmentUrl,
            createdAt = LocalDateTime.now(),
            isRead = false
        )

        return chatMessageRepository.save(message)
    }

    @Transactional
    fun markMessagesAsRead(roomIdString: String, userId: String) {
        val roomId = UUID.fromString(roomIdString)
        chatMessageRepository.markMessagesAsRead(roomId, userId)
        val participant = chatParticipantRepository.findByRoomIdAndUserUid(roomId, userId)
        if (participant != null) {
            participant.lastReadAt = LocalDateTime.now()
            chatParticipantRepository.save(participant)
        }
    }

    @Transactional
    fun deleteMessage(messageIdString: String, userId: String) {
        val messageId = UUID.fromString(messageIdString)
        val msg = chatMessageRepository.findById(messageId)
            .orElseThrow { IllegalArgumentException("Message not found: $messageId") }
        if (msg.sender.uid != userId) {
            throw IllegalAccessException("You can only delete your own messages")
        }
        chatMessageRepository.delete(msg)
    }

    @Transactional(readOnly = true)
    fun getRecipientId(roomIdString: String, senderId: String): String {
        val roomId = UUID.fromString(roomIdString)
        val participants = chatParticipantRepository.findByRoomId(roomId)
        val recipient = participants.firstOrNull { it.user.uid != senderId }
        return recipient?.user?.uid ?: ""
    }
}
