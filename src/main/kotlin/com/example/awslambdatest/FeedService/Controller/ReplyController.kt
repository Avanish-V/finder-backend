package com.iotabuild.campuscircle.FeedService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyRequest
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyResponse
import com.iotabuild.campuscircle.FeedService.Service.ReplyService
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reply")
class ReplyController (
    private val replyService: ReplyService,
    private val userRepository: UserRepository
){

    private fun getUser(request: HttpServletRequest): UserEntity {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        return userRepository.findById(token.uid).orElseThrow()
    }

    @PostMapping
    fun create(request: HttpServletRequest, @RequestBody replyRequest: ReplyRequest): ReplyResponse{
      return  replyService.create(userEntity = getUser(request), replyRequest = replyRequest)
    }


    @GetMapping("/{postId}")
    fun getReplies(request: HttpServletRequest,@PathVariable postId:Long): ResponseEntity<List<ReplyResponse>>{
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val result = replyService.getReplies(postId,token.uid)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/child/{postId}/{parentId}")
    fun getChildReplies(@PathVariable postId: Long,@PathVariable parentId: Long): ResponseEntity<List<ReplyResponse>>{
        val result = replyService.getChildReplies(postId,parentId)
        return ResponseEntity.ok(result)
    }


    @PostMapping("/{replyId}/{isLiked}")
    fun toggleLike(request: HttpServletRequest,@PathVariable replyId:Long, isLiked: Boolean): ResponseEntity<Map<String, Boolean>?> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val message = replyService.toggleLike(token.uid, replyId)
        return ResponseEntity.ok(mapOf("message" to message))
    }


    @DeleteMapping
    fun deleteReply(){

    }

}