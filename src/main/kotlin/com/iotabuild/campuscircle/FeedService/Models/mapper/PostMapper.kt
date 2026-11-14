package com.iotabuild.campuscircle.FeedService.Models.mapper

import com.iotabuild.campuscircle.FeedService.Models.DTOs.AuthorDetails
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostType
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostsEntity
import com.iotabuild.campuscircle.FeedService.Service.AttachmentType
import com.iotabuild.campuscircle.FeedService.Service.ImageAttachmentDto
import com.iotabuild.campuscircle.FeedService.Service.MediaPost
import com.iotabuild.campuscircle.FeedService.Service.PostRes
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

import com.iotabuild.campuscircle.FeedService.Service.VideoAttachmentDto
import com.iotabuild.campuscircle.FeedService.Service.DocumentAttachmentDto

@Component
class PostMapper {

    fun toResponse(
        post: PostsEntity,
        author: AuthorDetails,
        media: List<MediaPost>,
        isLiked: Boolean
    ): PostRes {

        val attachment = when (post.postType) {
            PostType.IMAGE -> {
                val sortedMedia = media.sortedBy { it.position }
                val hasWidths = sortedMedia.any { it.width != null }
                val hasHeights = sortedMedia.any { it.height != null }
                val hasRatios = sortedMedia.any { it.aspectRatio != null }

                ImageAttachmentDto(
                    type = AttachmentType.IMAGE,
                    images = sortedMedia.map { it.mediaUrl },
                    widths = if (hasWidths) sortedMedia.map { it.width ?: 0 } else null,
                    heights = if (hasHeights) sortedMedia.map { it.height ?: 0 } else null,
                    aspectRatios = if (hasRatios) sortedMedia.map { it.aspectRatio ?: 1f } else null
                )
            }
            PostType.VIDEO -> {
                val video = media.firstOrNull()
                if (video != null) {
                    VideoAttachmentDto(
                        type = AttachmentType.VIDEO,
                        videoUrl = video.mediaUrl,
                        thumbnailUrl = video.thumbnailUrl,
                        width = video.width,
                        height = video.height,
                        aspectRatio = video.aspectRatio
                    )
                } else null
            }
            PostType.DOCUMENT -> {
                val doc = media.firstOrNull()
                if (doc != null) {
                    DocumentAttachmentDto(
                        type = AttachmentType.DOCUMENT,
                        url = doc.mediaUrl,
                        name = doc.fileName ?: "Document.pdf",
                        thumbnailUrl = doc.thumbnailUrl,
                        size = doc.fileSize ?: 0L
                    )
                } else null
            }
            else -> null
        }

        return PostRes(
            postId = post.id.toString(),
            caption = post.caption.orEmpty(),
            attachment = attachment,
            author = author,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
            likesCount = post.likeCount.toInt(),
            commentCount = post.replyCount.toInt(),
            isLiked = isLiked
        )
    }
}