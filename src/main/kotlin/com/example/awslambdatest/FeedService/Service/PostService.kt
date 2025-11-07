package com.iotabuild.campuscircle.FeedService.Service

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.FeedService.Models.DTOs.AuthorDetails
import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostFilterType
import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostResponse
import com.iotabuild.campuscircle.FeedService.Models.Entity.*
import com.iotabuild.campuscircle.FeedService.Repository.MediaPostRepository
import com.iotabuild.campuscircle.FeedService.Repository.PollPostRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostLikeRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import com.iotabuild.campuscircle.FeedService.Repository.TextPostRepository
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
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
    private val postLikeRepository: PostLikeRepository
) {

    @Transactional
    fun createPost(author: UserEntity, request: CreatePostRequest,userId: String): PostResponse {

        val basePost = postsRepository.save(
            PostsEntity(
                postId = 0,
                author = author,
                postType = request.postType,
                campusId = request.campusId,
                visibility = request.visibility,
                feedMode = request.feedMode,
                content = request.text
            )
        )

        when (request.postType) {

            PostType.TEXT -> {
                postsRepository.save(basePost)
            }

            PostType.MEDIA -> {

                val mediaList = request.mediaUrl.map {
                    MediaPostEntity(
                        post = basePost,
                        mediaUrl = it,
                        mediaType = request.mediaType!!
                    )
                }
                basePost.mediaList.addAll(mediaList)

            }
            PostType.POLL -> {
                pollRepo.save(
                    PollPostEntity(
                        post = basePost,
                        question = request.question!!,
                        options = request.options!!,
                        postId = 0
                    )
                )
            }
        }

        return toResponse(basePost,userId)
    }

    fun getPostsByFeedMode(
        request: HttpServletRequest,
        feedMode: FeedMode,
        currentUserId: String,
        page: Int,
        size: Int
    ): Page<PostResponse> {

        val pageable = PageRequest.of(page, size, Sort.by("created_at").descending())
        val result = postsRepository.findPostsWithLikeStatus(PostFilterType.FEED_MODE, feedMode.name, currentUserId,pageable)
        return result.map { row ->
            mapRowToPostResponse(request, row)
        }
    }

    fun getUserPosts(
        request: HttpServletRequest,
        userId: String,
        currentUserId: String,
        page: Int,
        size: Int
    ): Page<PostResponse> {

        val pageable = PageRequest.of(page, size, Sort.by("created_at").descending())
        val result = postsRepository.findPostsWithLikeStatus(PostFilterType.AUTHOR_ID, userId, currentUserId,pageable)
        return result.map { row -> mapRowToPostResponse(request, row) }
    }


    fun getSinglePost(
        request: HttpServletRequest,
        postId: Long,
        currentUserId: String
    ): PostResponse {
        val row = postsRepository.findPostByIdWithLikeStatus(postId, currentUserId) ?: throw RuntimeException("Post not found")
        return mapRowToPostResponse(request, row)
    }


    private fun mapRowToPostResponse(request: HttpServletRequest, row: Map<String, Any>): PostResponse {

        val postId = (row["post_id"] as Number).toLong()

        return PostResponse(
            postId = postId,
            authorDetails = getAuthorDetails(request, row["author_id"], row["visibility"]),
            createdAt = (row["created_at"] as? Timestamp)?.time,
            updatedAt = (row["updated_at"] as? Timestamp)?.time,
            visibility = Visibility.valueOf(row["visibility"] as String),
            postType = PostType.valueOf(row["post_type"] as String),
            feedMode = FeedMode.valueOf(row["feed_mode"] as String),
            likes = (row["like_count"] as Number?)?.toLong() ?: 0L,
            isLiked = (row["is_liked"] as Boolean?) ?: false,
            comments = (row["reply_count"] as Number?)?.toLong() ?: 0L,
            text = row["content"] as? String,
            poll = getPollIfExists(postId),
            mediaPost = getMediaIfExists(postId)
        )


    }

    fun getAuthorDetails(request: HttpServletRequest, authorId: Any?, visibility: Any?): AuthorDetails? {
        val user =  userRepository.findById(authorId as  String)
        val token = request.getAttribute("firebaseUser") as FirebaseToken

        val author = user.map {

            if (visibility == "USER"){
                AuthorDetails(
                    authorId = it.uid,
                    authorName = it.name,
                    authorImage = it.image,
                    authorTagline = it.tagline,
                    isCurrentUser = token.uid == authorId
                )
            }else {
                AuthorDetails(
                    authorId = it.uid,
                    authorName = "Anonymous",
                    isCurrentUser = token.uid == authorId
                )
            }

        }

        return author.orElse(null)
    }

    private fun getTextContent(postId: Long): String? {
        val data = textRepo.findById(postId)
        return data.map { it.text }.orElse(null)
    }

    fun getPollIfExists(postId: Long): Poll? {
        val poll = pollRepo.findById(postId)
        return poll.map { Poll(question = it.question, options = it.options) }.orElse(null)
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
            .orElseThrow { IllegalArgumentException("Post not found") }

        // ✅ Ensure ownership
        if (post.author.uid != author) {
            throw IllegalAccessException("You cannot edit this post")
        }

        // ✅ Ensure it's a TEXT post
        if (post.postType != PostType.MEDIA) {
            throw IllegalStateException("Only text posts can be edited")
        }


        // ✅ Update content
        post.content = request
        post.updatedAt = LocalDateTime.now()

        return postsRepository.save(post)

    }


    fun togglePostLike(userId: String,postId: Long){

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val post = postsRepository.findById(postId)
            .orElseThrow { IllegalArgumentException("Post not found") }

        val existingReaction = postLikeRepository.findByUserUidAndPostPostId(userId, postId)

        when {
            existingReaction == null -> {
                // New reaction
                postLikeRepository.save(PostLikeEntity(user = user, post = post))
                post.likeCount = post.likeCount?.plus(1)
                postsRepository.save(post)
            }

            else -> {
                // Same reaction clicked again => remove it (toggle off)
                postLikeRepository.delete(existingReaction)
                post.likeCount = maxOf(post.likeCount?.minus(1) ?: 0, 0)
                postsRepository.save(post)
            }
        }

    }


    fun deletePost(postId: Long){
        return postsRepository.deleteById(postId)
    }

}
fun toResponse(post: PostsEntity,userId: String): PostResponse {

    val author = post.author // safe if @Transactional

    val isCurrentUser = author.uid == userId

    val authorDetails = if (post.visibility == Visibility.USER) {
        AuthorDetails(
            authorId = author.uid,
            authorName = author.name,
            authorImage = author.image,
            isVerified = false,
            isCurrentUser = isCurrentUser
        )
    } else AuthorDetails(
        authorId = author.uid,
        authorName = "Anonymous",
        isCurrentUser = isCurrentUser
    )



//        val mediaPost = if (post.postType == PostType.MEDIA) {
//            mediaRepo.findById(post.postId).orElse(null)?.let {
//                MediaPost(it.text, it.mediaUrl, it.mediaType)
//            }
//        } else null



    return PostResponse(
        postId = post.postId,
        authorDetails = authorDetails,
        text = post.content,
        createdAt = post.createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
        visibility = post.visibility,
        feedMode = post.feedMode,
        postType = post.postType,
        mediaPost = post.mediaList.map { MediaPost(mediaUrl = it.mediaUrl, mediaType = it.mediaType)},
        likes = post.likeCount ?: 0,
        comments = 0,
    )
}




data class CreatePostRequest(
    val postType: PostType,
    val campusId: String? = null,
    val visibility: Visibility = Visibility.USER,
    val feedMode: FeedMode = FeedMode.OPEN,


    // text
    val text: String? = null,

    // media
    val mediaUrl: List<String> = emptyList(),
    val mediaType: MediaType? = null,

    // poll
    val question: String? = null,
    val options: List<String>? = null
)


data class UpdatePostRequest(
    val text: String // required when editing
)

data class Poll(
    val question: String? = null,
    val options: List<String>? = null
)

data class MediaPost(
    val mediaUrl: String = "",
    val mediaType: MediaType? = null,
)


data class LikeResult(val changed: Boolean, val likeCount: Long)