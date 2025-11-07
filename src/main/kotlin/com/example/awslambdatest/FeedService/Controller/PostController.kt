package com.iotabuild.campuscircle.FeedService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostDataResponse
import com.iotabuild.campuscircle.FeedService.Models.DTOs.PostResponse
import com.iotabuild.campuscircle.FeedService.Models.Entity.FeedMode
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import com.iotabuild.campuscircle.FeedService.Service.CreatePostRequest
import com.iotabuild.campuscircle.FeedService.Service.PostService
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.awt.print.Pageable
import java.time.LocalDateTime

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
    private val userRepository: UserRepository,
    private val postRepository: PostsRepository
) {

    private fun getUser(request: HttpServletRequest): UserEntity {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        return userRepository.findById(token.uid).orElseThrow()
    }

    @PostMapping
    fun createPost(request: HttpServletRequest, @RequestBody body: CreatePostRequest): ResponseEntity<PostResponse>{
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val result =  postService.createPost(getUser(request), body,token.uid)
        return ResponseEntity.ok(result)
    }


    @GetMapping("/{feedMode}/{page}/{size}")
    fun getFeedPosts(
        @PathVariable feedMode: FeedMode,
        request: HttpServletRequest,
        @PathVariable page: Int = 0,
        @PathVariable size: Int = 10,
    ): ResponseEntity<Page<PostResponse>> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val posts =  postService.getPostsByFeedMode(request,feedMode, token.uid, page = page, size = size)
        return ResponseEntity.ok(posts)
    }

    @GetMapping("/postById/{uid}/{page}/{size}")
    fun getUserPosts(
        request: HttpServletRequest,
        @PathVariable uid: String,
        @PathVariable page: Int = 0,
        @PathVariable size: Int = 10,
    ): ResponseEntity<Page<PostResponse>> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val posts =  postService.getUserPosts(request,uid, token.uid, page = page, size = size)
        return ResponseEntity.ok(posts)
    }


    @GetMapping("/postById/{postId}")
    fun getSinglePost(request: HttpServletRequest,@PathVariable postId: Long):ResponseEntity<PostResponse>{
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val posts =  postService.getSinglePost(request,postId,token.uid)
        return ResponseEntity.ok(posts)
    }

    @PostMapping("like/{postId}")
    fun togglePostLike( request: HttpServletRequest,@PathVariable postId: Long){
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        postService.togglePostLike(userId = token.uid, postId = postId)
    }


    @DeleteMapping("/{postId}")
    fun deletePost(
        @PathVariable postId: Long,
        request: HttpServletRequest
    ): ResponseEntity<Void> {

        val firebaseUser = request.getAttribute("firebaseUser") as FirebaseToken
        val currentUid = firebaseUser.uid

        val post = postRepository.findById(postId).orElseThrow { RuntimeException("Post not found") }

        // Only allow author to delete
        if (post.author.uid != currentUid) {
            return ResponseEntity.status(403).build() // Forbidden
        }

        postRepository.deleteById(postId)

        return ResponseEntity.noContent().build()
    }


    @PatchMapping("/{postId}")
    fun editTextPost(
        @PathVariable postId: Long,
        @RequestBody body: EditPostRequest,
        request: HttpServletRequest
    ): ResponseEntity<EditResponse> {
        val currentUid = (request.getAttribute("firebaseUser") as FirebaseToken).uid
        val updated = postService.editTextPost(postId, currentUid, body.text)
        return ResponseEntity.ok(EditResponse(updated.content))
    }

    data class EditPostRequest(
        val text: String
    )

    data class EditResponse(
        val newText: String? = null,
        val editedAt: LocalDateTime = LocalDateTime.now()
    )
}