package com.iotabuild.campuscircle.FeedService.Service

import com.iotabuild.campuscircle.FeedService.Models.DTOs.LikeRequest
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostLikeEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostsEntity
import com.iotabuild.campuscircle.FeedService.Repository.PostLikeRepository
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

//@Service
//class LikeService (private val postLikeRepository: PostLikeRepository){
//
////    @Transactional
////    fun likePost(postsEntity: PostsEntity,userEntity: UserEntity): Boolean{
////      val entity =   postLikeRepository.save(
////            PostLikeEntity(
////                post = postsEntity,
////                user = userEntity
////            )
////      )
////      postLikeRepository.save(entity)
////      return true
////    }
//
//
//
//
//}