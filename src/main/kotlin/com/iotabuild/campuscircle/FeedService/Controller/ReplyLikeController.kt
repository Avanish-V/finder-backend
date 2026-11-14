package com.iotabuild.campuscircle.FeedService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.FeedService.Service.ReplyLikeService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/v1")
class ReplyLikeController(private val replyLikeService: ReplyLikeService) {

    @PostMapping("/replies/{replyId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun likeReply(
        request: HttpServletRequest,
        @PathVariable replyId: UUID
    ) {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        replyLikeService
            .likeReply(
            replyId = replyId,
            currentUserId = token.uid
        )
    }

    @DeleteMapping("/replies/{replyId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unlikeReply(
        request: HttpServletRequest,
        @PathVariable replyId: UUID
    ) {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        replyLikeService.unlikeReply(
            replyId = replyId,
            currentUserId = token.uid
        )
    }

}