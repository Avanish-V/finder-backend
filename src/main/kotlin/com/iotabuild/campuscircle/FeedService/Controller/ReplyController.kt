package com.iotabuild.campuscircle.FeedService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyRequest
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyResponse
import com.iotabuild.campuscircle.FeedService.Service.ReplyService
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/reply")
class ReplyController(
    private val replyService: ReplyService,
    private val userRepository: UserRepository
) {

    private fun getUser(request: HttpServletRequest): UserEntity {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        return userRepository.findById(token.uid).orElseThrow()
    }

    private fun getUid(request: HttpServletRequest): String {
        return (request.getAttribute("firebaseUser") as FirebaseToken).uid
    }

    @PostMapping
    fun create(
        request: HttpServletRequest,
        @RequestBody replyRequest: ReplyRequest
    ): ResponseEntity<ReplyResponse> {
        val response = replyService.create(userEntity = getUser(request), replyRequest = replyRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }


    @GetMapping("/{postId}")
    fun getReplies(
        request: HttpServletRequest,
        @PathVariable postId: Long
    ): ResponseEntity<List<ReplyResponse>> {
        val result = replyService.getReplies(postId, getUid(request))
        return ResponseEntity.ok(result)
    }


    @GetMapping("/child/{postId}/{parentId}")
    fun getChildReplies(
        @PathVariable postId: Long,
        @PathVariable parentId: Long
    ): ResponseEntity<List<ReplyResponse>> {
        val result = replyService.getChildReplies(postId, parentId)
        return ResponseEntity.ok(result)
    }


    /**
     * Toggle like on a reply.
     * Mobile calls: POST reply/{replyId}/{isLiked}
     */
    @PostMapping("/{replyId}/{isLiked}")
    fun toggleLike(
        request: HttpServletRequest,
        @PathVariable replyId: Long,
        @PathVariable isLiked: Boolean          // fix: was missing @PathVariable annotation
    ): ResponseEntity<Map<String, Any>> {
        val uid = getUid(request)
        val nowLiked = replyService.toggleLike(uid, replyId)
        return ResponseEntity.ok(mapOf("liked" to nowLiked))
    }


    /**
     * Delete a reply owned by the current user.
     * Mobile calls: DELETE reply/{replyId}
     */
    @DeleteMapping("/{replyId}")
    fun deleteReply(
        request: HttpServletRequest,
        @PathVariable replyId: Long
    ): ResponseEntity<Void> {
        try {
            replyService.deleteReply(replyId, getUid(request))
        } catch (ex: IllegalAccessException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, ex.message)
        } catch (ex: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ex.message)
        }
        return ResponseEntity.noContent().build()
    }
}