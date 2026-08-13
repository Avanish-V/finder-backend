package com.iotabuild.campuscircle.FeedService.Service

import com.iotabuild.campuscircle.FeedService.Models.DTOs.AuthorDetails
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyRequest
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyResponse
import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyLikeEntity
import com.iotabuild.campuscircle.FeedService.Models.Entity.Visibility
import com.iotabuild.campuscircle.FeedService.Repository.LikeReplyRepository
import com.iotabuild.campuscircle.FeedService.Repository.ReplyRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.Notification.event.PostCommentedEvent
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class ReplyService (
    private val replyRepository: ReplyRepository,
    private val likeReplyRepository: LikeReplyRepository,
    private val postsRepository: PostsRepository,
    private val publisher: ApplicationEventPublisher
){

    @Transactional
    fun create(userEntity: UserEntity, replyRequest: ReplyRequest): ReplyResponse {
        val replyEntity = ReplyEntity(
            postId = replyRequest.postId,
            author = userEntity,
            caption = replyRequest.caption,
            mediaUrl = replyRequest.mediaUrl ?: "",
            parentId = replyRequest.parentId,
        )
        val responseData = replyRepository.save(replyEntity)

        // Increment post reply count
        postsRepository.findById(replyRequest.postId).ifPresent { post ->
            post.replyCount = (post.replyCount ?: 0) + 1
            postsRepository.save(post)

            if (userEntity.uid != post.author.uid) {
                publisher.publishEvent(
                    PostCommentedEvent(
                        senderUid = userEntity.uid,
                        receiverUid = post.author.uid,
                        postId = post.postId
                    )
                )
            }
        }

        return toReplyResponse(responseData, authorId = userEntity.uid)
    }

    fun getReplies(postId: Long, currentUserId: String): List<ReplyResponse> {

        val replies = replyRepository.findByPostId(postId).sortedBy { it.createdAt }

        if (replies.isEmpty()) return emptyList()

        // 1. Build set of liked reply IDs for current user
        val replyIds = replies.mapNotNull { it.replyId }
        val likedReplyIds = likeReplyRepository
            .findAllByUserIdAndReply_ReplyIdIn(currentUserId, replyIds)
            .map { it.reply.replyId }
            .toSet()

        // 2. Group replies by parentId for tree building
        val repliesByParent = replies.groupBy { it.parentId }

        // 3. Recursively build reply tree
        fun buildReplyTree(parentId: Long?): List<ReplyResponse> {
            return repliesByParent[parentId]?.map { reply ->
                val children = buildReplyTree(reply.replyId)
                toReplyResponse(reply, currentUserId, likedReplyIds).copy(children = children)
            } ?: emptyList()
        }

        // 4. Return top-level replies (parentId = null)
        return buildReplyTree(null)
    }


    fun getChildReplies(postId: Long,parentId:Long):List<ReplyResponse>{
       return replyRepository.findByPostIdAndParentId(postId,parentId).map { toReplyResponse(it,"", emptySet()) }
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

    @Transactional
    fun deleteReply(replyId: Long, currentUid: String) {
        val reply = replyRepository.findById(replyId)
            .orElseThrow { IllegalArgumentException("Reply not found") }
        if (reply.author.uid != currentUid) {
            throw IllegalAccessException("You cannot delete this reply")
        }

        // Decrement post reply count
        postsRepository.findById(reply.postId).ifPresent { post ->
            post.replyCount = maxOf(0, (post.replyCount ?: 0) - 1)
            postsRepository.save(post)
        }

        likeReplyRepository.deleteByReplyReplyIdIn(listOf(replyId))
        replyRepository.delete(reply)
    }


}

private fun toReplyResponse(post: ReplyEntity, authorId: String, likedReplyIds: Set<Long?> = emptySet()): ReplyResponse {

    val author = post.author

    val authorDetails = AuthorDetails(
        authorId = author.uid,
        authorName = author.name,
        authorImage = author.image ?: "",
        authorTagline = "",// mobile expects non-null String
        isVerified = false,
        isCurrentUser = if (author.uid == authorId) true else false
    )

    return ReplyResponse(
        replyId = post.replyId?.toString() ?: "",  // mobile expects String
        postId = post.postId.toString(),             // mobile expects String
        author = authorDetails,
        caption = post.caption,                         // entity field is "text"; mobile expects "caption"
        mediaUrl = post.mediaUrl.ifEmpty { null },  // return null instead of empty string
        parentId = post.parentId,
        isEdited = post.updatedAt.toEpochMilli() != post.createdAt.toEpochMilli(),
        updatedAt = post.updatedAt.toEpochMilli(),
        createdAt = post.createdAt.toEpochMilli(),
        likesCount = post.likes.toInt(),
        isLiked = if (post.replyId != null) likedReplyIds.contains(post.replyId) else false
    )
}