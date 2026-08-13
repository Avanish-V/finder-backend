package com.iotabuild.campuscircle.ChatFeature.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.ChatFeature.Models.ChatMessageDTO
import com.iotabuild.campuscircle.ChatFeature.Models.UserChatsDTO
import com.iotabuild.campuscircle.ChatFeature.Service.ChatService
import com.iotabuild.campuscircle.realtime.model.ChatMessageCreatedPayload
import com.iotabuild.campuscircle.realtime.model.SocketEvent
import com.iotabuild.campuscircle.realtime.model.SocketEventType
import com.iotabuild.campuscircle.realtime.sender.SocketSender
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService,
    private val chatParticipantRepository: com.iotabuild.campuscircle.ChatFeature.Repository.ChatParticipantRepository,
    private val socketSender: SocketSender
) {

    @GetMapping("/rooms")
    fun getRooms(request: HttpServletRequest): ResponseEntity<List<UserChatsDTO>> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val rooms = chatService.getChats(token.uid)
        return ResponseEntity.ok(rooms)
    }

    @GetMapping("/rooms/private/{recipientId}")
    fun getOrCreatePrivateRoom(
        request: HttpServletRequest,
        @PathVariable recipientId: String
    ): ResponseEntity<Map<String, String>> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val roomId = chatService.fetchOrCreatePrivateRoom(token.uid, recipientId)
        return ResponseEntity.ok(mapOf("roomId" to roomId))
    }

    @GetMapping("/rooms/{roomId}/messages")
    fun getMessages(
        request: HttpServletRequest,
        @PathVariable roomId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<*> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        return try {
            val messages = chatService.getMessages(roomId, token.uid, page, size)
            ResponseEntity.ok(messages)
        } catch (e: AccessDeniedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("error" to "You are not a participant in this chat room"))
        }
    }

    @PostMapping("/rooms/{roomId}/read")
    fun markAsRead(
        request: HttpServletRequest,
        @PathVariable roomId: String
    ): ResponseEntity<Map<String, Boolean>> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        chatService.markMessagesAsRead(roomId, token.uid)
        return ResponseEntity.ok(mapOf("success" to true))
    }

    @PostMapping("/rooms/{roomId}/messages")
    fun sendMessage(
        request: HttpServletRequest,
        @PathVariable roomId: String,
        @RequestBody body: SendMessageRequest
    ): ResponseEntity<ChatMessageDTO> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val saved = chatService.saveMessage(roomId, token.uid, body.text, body.attachmentUrl)
        
        val timestamp = saved.createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val msgDto = ChatMessageDTO(
            messageId = saved.id.toString(),
            senderId = token.uid,
            text = saved.text,
            attachmentUrl = saved.attachmentUrl,
            timestamp = timestamp,
            read = saved.isRead
        )

        // Broadcast via SocketSender to recipient
        val participants = chatParticipantRepository.findByRoomId(saved.room.id)
        val recipient = participants.firstOrNull { it.user.uid != token.uid }
        if (recipient != null) {
            val socketEvent = SocketEvent(
                eventType = SocketEventType.CHAT_MESSAGE,
                payload = ChatMessageCreatedPayload(
                    messageId = saved.id.toString(),
                    roomId = roomId,
                    senderUid = token.uid,
                    text = saved.text,
                    attachmentUrl = saved.attachmentUrl,
                    timestamp = timestamp,
                    read = saved.isRead
                )
            )
            socketSender.sendToUser(recipient.user.uid, socketEvent)
        }

        return ResponseEntity.ok(msgDto)
    }

    @DeleteMapping("/messages/{messageId}")
    fun deleteMessage(
        request: HttpServletRequest,
        @PathVariable messageId: String
    ): ResponseEntity<Map<String, Boolean>> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        chatService.deleteMessage(messageId, token.uid)
        return ResponseEntity.ok(mapOf("success" to true))
    }
}

data class SendMessageRequest(
    val text: String,
    val attachmentUrl: String?
)
