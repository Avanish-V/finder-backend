package com.iotabuild.campuscircle.FeedService.Service

import com.iotabuild.campuscircle.FeedService.Models.Entity.PostLikeEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostsEntity
import com.iotabuild.campuscircle.FeedService.Repository.PostLikeRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import com.iotabuild.campuscircle.Notification.event.PostLikedEvent
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PostLikeService (
    private val userQueryService: UserQueryService,
    private val postQueryService: PostQueryService,
    private val postLikeRepository: PostLikeRepository,
    private val postsRepository: PostsRepository,
    private val publisher : ApplicationEventPublisher
){

    @Transactional
    fun togglePostLike(userId: String, postId: Long) {

        val user = userQueryService.getUserOrThrow(userId)

        val post = postQueryService.getPostOrThrow(postId)

        val isLiked = toggleLike(user, post)

        publishLikeEventIfNeeded(
            isLiked,
            user,
            post
        )
    }


    private fun toggleLike(user: UserEntity, post: PostsEntity): Boolean {

        val existingLike =
            postLikeRepository.findByUserUidAndPostPostId(
                user.uid,
                post.postId
            )

        return if (existingLike == null) {

            addLike(user, post)

            true

        } else {

            removeLike(existingLike, post)

            false
        }
    }

    private fun addLike(user: UserEntity, post: PostsEntity) {

        val like = PostLikeEntity(
            user = user,
            post = post
        )
        val savedLike = postLikeRepository.save(like)

        post.likes.add(savedLike)

        post.likeCount = (post.likeCount ?: 0) + 1

        postsRepository.save(post)
    }

    private fun removeLike(like: PostLikeEntity, post: PostsEntity) {

        post.likes.removeIf { it.id == like.id || it == like }

        postLikeRepository.delete(like)

        post.likeCount =
            maxOf(
                (post.likeCount ?: 0) - 1,
                0
            )

        postsRepository.save(post)
    }

    private fun publishLikeEventIfNeeded(isLiked: Boolean, user: UserEntity, post: PostsEntity) {

        if (!isLiked) return

        if (user.uid == post.author.uid) return

        publisher.publishEvent(

            PostLikedEvent(

                senderUid = user.uid,

                receiverUid = post.author.uid,

                postId = post.postId
            )
        )
    }

}