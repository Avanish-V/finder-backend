package com.iotabuild.campuscircle.FeedService.Service

import com.iotabuild.campuscircle.Common.exception.ReplyNotFoundException
import com.iotabuild.campuscircle.Common.exception.UserNotFoundException
import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyLikeEntity
import com.iotabuild.campuscircle.FeedService.Repository.PostLikeRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import com.iotabuild.campuscircle.FeedService.Repository.ReplyLikeRepository
import com.iotabuild.campuscircle.FeedService.Repository.ReplyRepository
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReplyLikeService(
    private val replyLikeRepository: ReplyLikeRepository,
    private val replyRepository: ReplyRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun likeReply(
        replyId: UUID,
        currentUserId: String
    ) {
        val reply = replyRepository
            .findByIdAndDeletedFalse(replyId)
            ?: throw ReplyNotFoundException(replyId)

        val alreadyLiked =
            replyLikeRepository.existsByUserUidAndReplyId(
                replyId = replyId,
                userUid = currentUserId
            )

        if (alreadyLiked) {
            return
        }

        val user = userRepository
            .findById(currentUserId)
            .orElseThrow {
                UserNotFoundException(currentUserId)
            }

        val like = ReplyLikeEntity(
            reply = reply,
            user = user
        )

        replyLikeRepository.save(like)

        reply.likesCount += 1


    }

    @Transactional
    fun unlikeReply(
        replyId: UUID,
        currentUserId: String
    ) {
       val reply =  replyRepository
            .findByIdAndDeletedFalse(replyId)
            ?: throw ReplyNotFoundException(replyId)

        replyLikeRepository.deleteByUserUidAndReplyId(
            replyId = replyId,
            userUid = currentUserId
        )
        reply.likesCount -= 1
    }
}