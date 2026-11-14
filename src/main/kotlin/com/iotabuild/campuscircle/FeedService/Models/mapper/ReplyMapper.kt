package com.iotabuild.campuscircle.FeedService.Models.mapper

import com.iotabuild.campuscircle.FeedService.Models.DTOs.AuthorDetails
import com.iotabuild.campuscircle.FeedService.Models.DTOs.MentionedUser
import com.iotabuild.campuscircle.FeedService.Models.DTOs.ReplyResponse
import com.iotabuild.campuscircle.FeedService.Models.Entity.ReplyEntity
import org.springframework.stereotype.Component

@Component
class ReplyMapper {

    fun toResponse(
        reply: ReplyEntity,
        currentUserId: String,
        isLiked: Boolean,
        likesCount: Long,
        childCount: Long = 0
    ): ReplyResponse {

        return ReplyResponse(
            id = requireNotNull(reply.id),

            content = if (reply.deleted) {
                ""
            } else {
                reply.content
            },

            imageUrl = if (reply.deleted) {
                null
            } else {
                reply.imageUrl
            },
            author = AuthorDetails(
                authorId = reply.author.uid,
                authorName = reply.author.name,
                authorImage = reply.author.image ?:"",
                isCurrentUser = reply.author.uid == currentUserId

            ),
            mentionedUser = reply.mentionedUser?.let {
                MentionedUser(
                    uid = it.uid,
                    name = it.name,
                    image = it.image
                )
            },
            parentReplyId = reply.parent?.id,
            createdAt = reply.createdAt,
            updatedAt = reply.updatedAt,
            isLiked = isLiked,
            likesCount = likesCount,
            childCount = childCount,
            isAuthor = reply.author.uid == currentUserId,
            deleted = reply.deleted
        )
    }
}