package com.iotabuild.campuscircle.FeedService.Service

import com.iotabuild.campuscircle.FeedService.Models.DTOs.Actions
import com.iotabuild.campuscircle.FeedService.Models.DTOs.AuthorDetails
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyRequest
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyResponse
import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyLikeEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.Visibility
import com.iotabuild.campuscircle.FeedService.Repository.LikeReplyRepository
import com.iotabuild.campuscircle.FeedService.Repository.ReplyRepository
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ReplyService (
    private val replyRepository: ReplyRepository,
    private val likeReplyRepository: LikeReplyRepository
){

    @Transactional
    fun create(userEntity: UserEntity,replyRequest: ReplyRequest): ReplyResponse{
       val replyEntity = ReplyEntity(
           postId = replyRequest.postId,
           author = userEntity,
           text = replyRequest.text,
           visibility = replyRequest.visibility,
           mediaUrl = replyRequest.mediaUrl,
           parentId = replyRequest.parentId,
       )
       val responseData =  replyRepository.save(replyEntity)
       return toReplyResponse(responseData)
    }

    fun getReplies(postId: Long, currentUserId: String): List<ReplyResponse> {

        val replies = replyRepository.findByPostId(postId).sortedBy { it.createdAt }

        if (replies.isEmpty()) return emptyList()

        // 1️⃣ Build map of replyId → liked status
        val replyIds = replies.mapNotNull { it.replyId }

        val likedReplies = likeReplyRepository.findAllByUserIdAndReply_ReplyIdIn(currentUserId, replyIds)
            .map { it.reply.replyId }
            .toSet()

        // 2️⃣ Group replies by parentId
        val repliesByParent = replies.groupBy { it.parentId }

        // 3️⃣ Recursive builder function
        fun buildReplyTree(parentId: Long?): List<ReplyResponse> {
            return repliesByParent[parentId]?.map { reply ->
                val children = buildReplyTree(reply.replyId)
                val isLiked = likedReplies.contains(reply.replyId)
                toReplyResponse(reply).copy(children = children, actions = Actions(isLiked = isLiked, likesCount = reply.likes.toInt()))
            } ?: emptyList()
        }

        // 4️⃣ Build the tree starting from top-level replies (parentId = null)
        return buildReplyTree(null)
    }


    fun getChildReplies(postId: Long,parentId:Long):List<ReplyResponse>{
       return replyRepository.findByPostIdAndParentId(postId,parentId).map { toReplyResponse(it) }
    }

    fun likeIncrement(replyId: Long){
        replyRepository.likeIncrement(replyId)
    }

    fun likeDecrement(replyId: Long){
        replyRepository.likeIncrement(replyId)
    }


//    @Transactional
//    fun addLike(replyId: Long, userId: String): String {
//        if (likeReplyRepository.existsByUserIdAndReply_Id(userId, replyId)) {
//            return "User already liked this reply"
//        }
//
//        val reply = likeReplyRepository.findById(replyId).orElseThrow { IllegalArgumentException("Reply not found") }
//
//        likeReplyRepository.save(ReplyLikeEntity(
//            userId = userId,
//            reply = reply
//        ))
//        replyRepository.likeIncrement(replyId)
//        return "Like added successfully"
//    }
//
//    @Transactional
//    fun removeLike(replyId: Long, userId: String): String {
//        if (!likeReplyRepository.existsByUserIdAndReply_Id(userId, replyId)) {
//            return "User has not liked this reply"
//        }
//
//        likeReplyRepository.deleteByUserIdAndReply_Id(userId, replyId)
//        replyRepository.likeDecrement(replyId)
//        return "Like removed successfully"
//    }

    @Transactional
    fun toggleLike(userId: String, replyId: Long): Boolean {
        val reply = replyRepository.findById(replyId)
            .orElseThrow { IllegalArgumentException("Reply not found") }

        val alreadyLiked = likeReplyRepository.existsByUserIdAndReply_ReplyId(userId, replyId)

        return if (alreadyLiked) {
            // Remove like
            likeReplyRepository.deleteByUserIdAndReply_ReplyId(userId, replyId)
            reply.likes = maxOf(reply.likes - 1, 0)
            replyRepository.save(reply)
            false
        } else {
            // Add like
            likeReplyRepository.save(ReplyLikeEntity(userId = userId, reply = reply))
            reply.likes += 1
            replyRepository.save(reply)
            true
        }
    }


    fun getLikes(replyId: Long): Int {
        return likeReplyRepository.countByReply_ReplyId(replyId)
    }

    fun deleteReply(replyEntity: ReplyEntity){
         replyRepository.delete(replyEntity)
    }


}

private fun toReplyResponse(post: ReplyEntity): ReplyResponse {

    val author = post.author // safe if @Transactional

    val authorDetails = if (post.visibility == Visibility.USER) {
        AuthorDetails(
            authorId = author.uid,
            authorName = author.name,
            authorImage = author.image,
            isVerified = false
        )
    } else AuthorDetails(
        authorId = author.uid,
        authorName = "Anonymous"
    )

    return ReplyResponse(
        replyId = post.replyId,
        postId = post.postId,
        author = authorDetails,
        visibility = post.visibility,
        text = post.text,
        mediaUrl = post.mediaUrl,
        parentId = post.parentId,
        isEdited = post.updatedAt.toEpochMilli() != post.createdAt.toEpochMilli(),
        updatedAt = post.updatedAt.toEpochMilli(),
        createdAt = post.createdAt.toEpochMilli(),
        actions = Actions(
            likesCount = post.likes.toInt(),
            replyCount = post.replies.toInt()
        )
    )
}