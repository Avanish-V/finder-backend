package com.iotabuild.campuscircle.FeedService.Service

import com.iotabuild.campuscircle.FeedService.Models.DTOs.AuthorDetails
import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostFilterType
import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostResponse
import com.iotabuild.campuscircle.FeedService.Models.Entity.*
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.iotabuild.campuscircle.FeedService.Repository.MediaPostRepository
import com.iotabuild.campuscircle.FeedService.Repository.PollPostRepository
import com.iotabuild.campuscircle.FeedService.Repository.PollOptionRepository
import com.iotabuild.campuscircle.FeedService.Repository.PollVoteRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostLikeRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import com.iotabuild.campuscircle.FeedService.Repository.TextPostRepository
import com.iotabuild.campuscircle.FeedService.Repository.ReplyRepository
import com.iotabuild.campuscircle.FeedService.Repository.LikeReplyRepository
import com.iotabuild.campuscircle.Notification.event.PostLikedEvent
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class PostService(
    private val postsRepository: PostsRepository,
    private val userRepository: UserRepository,
    private val textRepo: TextPostRepository,
    private val mediaRepo: MediaPostRepository,
    private val pollRepo: PollPostRepository,
    private val pollOptionRepo: PollOptionRepository,
    private val pollVoteRepo: PollVoteRepository,
    private val postLikeRepository: PostLikeRepository,
    private val replyRepository: ReplyRepository,
    private val likeReplyRepository: LikeReplyRepository,

    private val publisher: ApplicationEventPublisher
) {

    @Transactional
    fun createPost(author: UserEntity, request: CreatePostRequest, userId: String): PostRes {

        val postType = when (request.attachment) {
            is ImageAttachmentDto -> PostType.MEDIA
            else -> PostType.TEXT
        }

        val basePost = postsRepository.save(
            PostsEntity(
                postId = 0,
                author = author,
                postType = postType,
                caption = request.caption
            )
        )

        when (val att = request.attachment) {
            is ImageAttachmentDto -> {
                val mediaList = att.images.map {
                    MediaPostEntity(
                        post = basePost,
                        mediaUrl = it,
                        mediaType = MediaType.IMAGE
                    )
                }
                basePost.mediaList.addAll(mediaList)
                postsRepository.save(basePost)
            }
            null -> {
                // Nothing extra for TEXT post
            }
        }

        return toPostRes(basePost, userId)
    }

    fun getAllPosts(
        currentUserId: String,
        page: Int,
        size: Int
    ): Page<PostRes> {

        val pageable = PageRequest.of(page, size, Sort.by("created_at").descending())
        val result = postsRepository.findPostsWithLikeStatus(PostFilterType.ALL, "", currentUserId,pageable)
        return result.map { row ->
            mapRowToPostRes(currentUserId, row)
        }
    }

    fun getUserPosts(
        userId: String,
        currentUserId: String,
        page: Int,
        size: Int
    ): Page<PostRes> {

        val pageable = PageRequest.of(page, size, Sort.by("created_at").descending())
        val result = postsRepository.findPostsWithLikeStatus(PostFilterType.AUTHOR_ID, userId, currentUserId,pageable)
        return result.map { row -> mapRowToPostRes(currentUserId, row) }
    }


    fun getSinglePost(
        postId: Long,
        currentUserId: String
    ): PostRes {
        val row = postsRepository.findPostByIdWithLikeStatus(postId, currentUserId) ?: throw RuntimeException("Post not found")
        return mapRowToPostRes(currentUserId, row)
    }

    private fun formatTimestamp(value: Any?): String {
        if (value == null) return ""
        return when (value) {
            is Timestamp -> value.time.toString()
            is LocalDateTime -> value.toInstant(java.time.ZoneOffset.UTC).toEpochMilli().toString()
            is java.time.Instant -> value.toEpochMilli().toString()
            is Number -> value.toLong().toString()
            else -> value.toString()
        }
    }

    private fun mapRowToPostRes(currentUserId: String, row: Map<String, Any>): PostRes {
        val postId = (row["post_id"] as Number).toLong()
        val authorIdStr = row["author_id"] as? String ?: ""
        val isCurrentUser = currentUserId == authorIdStr

        val authorDetails = getAuthorDetails(currentUserId, authorIdStr,) ?: AuthorDetails(
            authorId = authorIdStr,
            authorName = "Anonymous",
            isVerified = false,
            isCurrentUser = isCurrentUser
        )

        val postType = PostType.valueOf(row["post_type"] as String)
        val attachment = when (postType) {
            PostType.MEDIA -> {
                ImageAttachmentDto(
                    type = AttachmentType.IMAGE,
                    images = getMediaIfExists(postId).map { it.mediaUrl }
                )
            }
            else -> null
        }

        val likesCount = (row["like_count"] as? Number)?.toInt() ?: 0
        val commentCount = (row["reply_count"] as? Number)?.toInt() ?: 0
        val isLiked = row["is_liked"] as? Boolean ?: false

        return PostRes(
            postId = postId.toString(),
            caption = row["caption"] as? String ?: "",
            attachment = attachment,
            author = authorDetails,
            createdAt = formatTimestamp(row["created_at"]),
            updatedAt = formatTimestamp(row["updated_at"]),
            likesCount = likesCount,
            commentCount = commentCount,
            isLiked = isLiked
        )
    }

    fun mapToAuthorDetails(author: UserEntity, currentUserId: String): AuthorDetails {
        val isCurrentUser = author.uid == currentUserId
        return AuthorDetails(
            authorId = author.uid,
            authorName = author.name,
            authorImage = author.image ?: "",
            authorTagline = author.tagline,
            isVerified = author.isVerified,
            isCurrentUser = isCurrentUser
        )
    }

    fun getAuthorDetails(currentUserId: String, authorId: Any?): AuthorDetails? {
        val authorIdStr = authorId as? String ?: return null
        val user =  userRepository.findById(authorIdStr)


        val author = user.map {
            mapToAuthorDetails(it, currentUserId)
        }

        return author.orElse(null)
    }

    private fun getTextContent(postId: Long): String? {
        val data = textRepo.findById(postId)
        return data.map { it.text }.orElse(null)
    }

    fun getPollIfExists(postId: Long, currentUserId: String? = null): Poll? {
        val pollOpt = pollRepo.findById(postId)
        if (pollOpt.isEmpty) return null
        val poll = pollOpt.get()
        
        val userVote = currentUserId?.let { pollVoteRepo.findByPollPostIdAndUserUid(postId, it) }
        
        return Poll(
            question = poll.question,
            options = poll.options.map { it.optionText },
            pollOptions = poll.options.map { option ->
                PollOptionDTO(
                    optionId = option.id,
                    text = option.optionText,
                    voteCount = option.votes.size.toLong()
                )
            },
            totalVotes = poll.options.sumOf { it.votes.size }.toLong(),
            hasVoted = userVote != null,
            selectedOptionId = userVote?.option?.id
        )
    }

    fun getMediaIfExists(postId: Long): List<MediaPost> {
        val mediaList = mediaRepo.findAllByPostPostId(postId) // see note below
        if (mediaList.isEmpty()) return emptyList()
        return mediaList.map {
            MediaPost(
                mediaUrl = it.mediaUrl,
                mediaType = it.mediaType
            )
        }
    }



    @Transactional
    fun editTextPost(postId: Long, author: String?, request: String): PostsEntity {
        val post = postsRepository.findById(postId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found") }

        // ✅ Ensure ownership
        if (post.author.uid != author) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this post")
        }

        // ✅ Ensure it's not a poll post
        if (post.postType != PostType.TEXT && post.postType != PostType.MEDIA) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Only text and media posts can be edited")
        }


        // ✅ Update content
        post.caption = request
        post.updatedAt = LocalDateTime.now()

        return postsRepository.save(post)

    }


    @Transactional
    fun deletePost(postId: Long, currentUid: String) {
        val post = postsRepository.findById(postId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found") }

        // Only allow author to delete
        if (post.author.uid != currentUid) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this post")
        }

        // 1. Delete reply likes and replies associated with this post
        val replies = replyRepository.findByPostId(postId)
        if (replies.isNotEmpty()) {
            val replyIds = replies.mapNotNull { it.replyId }
            if (replyIds.isNotEmpty()) {
                likeReplyRepository.deleteByReplyReplyIdIn(replyIds)
            }
            replyRepository.deleteAll(replies)
        }

        // 2. Delete poll posts, options, votes
        val poll = pollRepo.findById(postId)
        if (poll.isPresent) {
            pollRepo.delete(poll.get())
        }

        // 3. Delete text posts
        val textPost = textRepo.findById(postId)
        if (textPost.isPresent) {
            textRepo.delete(textPost.get())
        }

        // 4. Delete the post itself
        postsRepository.delete(post)
    }

    @Transactional
    fun voteOnPoll(postId: Long, optionId: Long, userId: String): Poll {
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val poll = pollRepo.findById(postId).orElseThrow { RuntimeException("Poll not found") }
        
        // Check if already voted
        val existingVote = pollVoteRepo.findByPollPostIdAndUserUid(postId, userId)
        if (existingVote != null) {
            throw RuntimeException("You have already voted on this poll")
        }
        
        val option = poll.options.find { it.id == optionId } ?: throw RuntimeException("Option not found")
        
        pollVoteRepo.save(PollVoteEntity(poll = poll, option = option, user = user))
        
        return getPollIfExists(postId, userId)!!
    }

    fun toResponse(post: PostsEntity, userId: String): PostResponse {
        val author = post.author
        val authorDetails = mapToAuthorDetails(author, userId)

        return PostResponse(
            postId = post.postId,
            authorDetails = authorDetails,
            text = post.caption,
            createdAt = post.createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
            postType = post.postType,
            mediaPost = post.mediaList.map { MediaPost(mediaUrl = it.mediaUrl, mediaType = it.mediaType) },
            likes = post.likeCount ?: 0,
            comments = post.replyCount ?: 0,
            poll = getPollIfExists(post.postId, userId)
        )
    }

    fun toPostRes(post: PostsEntity, userId: String): PostRes {
        val author = post.author
        val authorDetails = mapToAuthorDetails(author, userId)

        val attachment = when (post.postType) {
            PostType.MEDIA -> {
                ImageAttachmentDto(
                    type = AttachmentType.IMAGE,
                    images = post.mediaList.map { it.mediaUrl }
                )
            }
            else -> null
        }

        return PostRes(
            postId = post.postId.toString(),
            caption = post.caption ?: "",
            attachment = attachment,
            author = authorDetails,
            createdAt = post.createdAt.toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
            updatedAt = post.updatedAt.toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
            likesCount = post.likeCount?.toInt() ?: 0,
            commentCount = post.replyCount?.toInt() ?: 0,
            isLiked = false
        )
    }

}




enum class AttachmentType {
    IMAGE, VIDEO, POLL
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ImageAttachmentDto::class, name = "IMAGE")
)
interface AttachmentDto

data class ImageAttachmentDto(
    val type: AttachmentType = AttachmentType.IMAGE,
    val images: List<String>
) : AttachmentDto

data class CreatePostRequest(
    val caption: String,
    val attachment: AttachmentDto? = null,
    val visibility: Visibility? = Visibility.USER
)

data class PostRes(
    val postId: String,
    val caption: String,
    val attachment: AttachmentDto? = null,
    val author: AuthorDetails,
    val createdAt: String,
    val updatedAt: String,
    val likesCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false
)

data class Poll(
    val question: String? = null,
    val options: List<String>? = null,
    val pollOptions: List<PollOptionDTO> = emptyList(),
    val totalVotes: Long = 0,
    val hasVoted: Boolean = false,
    val selectedOptionId: Long? = null
)

data class PollOptionDTO(
    val optionId: Long,
    val text: String,
    val voteCount: Long
)

data class MediaPost(
    val mediaUrl: String = "",
    val mediaType: MediaType? = null,
)