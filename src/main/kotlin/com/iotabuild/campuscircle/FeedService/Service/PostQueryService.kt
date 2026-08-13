package com.iotabuild.campuscircle.FeedService.Service

import com.iotabuild.campuscircle.Common.exception.PostNotFoundException
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostsEntity
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import org.springframework.stereotype.Service

@Service
class PostQueryService(
    private val postRepository: PostsRepository
) {

    fun getPostOrThrow(postId: Long): PostsEntity {
        return postRepository.findById(postId)
            .orElseThrow {
                PostNotFoundException(postId)
            }
    }
}