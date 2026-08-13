package com.iotabuild.campuscircle.FeedService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.FeedService.Models.DTOs.LikeRequest
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

//@RestController
//@RequestMapping("/like")
//class LikeController(
//    private val likeService: LikeService,
//    private val userRepository: UserRepository,
//    private val postsRepository: PostsRepository
//) {
//
////    private fun getUser(request: HttpServletRequest): UserEntity {
////        val token = request.getAttribute("firebaseUser") as FirebaseToken
////        return userRepository.findById(token.uid).orElseThrow()
////    }
////
////    @PostMapping()
////    fun likePost(request: HttpServletRequest,@RequestBody likeRequest: LikeRequest){
////        val post = postsRepository.findByPostId(likeRequest.postId.toLong())
//////        likeService.likePost(
//////            postsEntity = post,
//////            userEntity = getUser(request)
//////        )
////    }
//
//
//}