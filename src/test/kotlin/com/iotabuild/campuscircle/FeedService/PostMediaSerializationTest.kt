package com.iotabuild.campuscircle.FeedService

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.iotabuild.campuscircle.FeedService.Models.DTOs.AuthorDetails
import com.iotabuild.campuscircle.FeedService.Models.Entity.MediaType
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostType
import com.iotabuild.campuscircle.FeedService.Models.Entity.PostsEntity
import com.iotabuild.campuscircle.FeedService.Models.mapper.PostMapper
import com.iotabuild.campuscircle.FeedService.Service.*
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class PostMediaSerializationTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()
    private val mapper: PostMapper = PostMapper()

    @Test
    fun testDeserializeCreatePostRequestWithVideoAttachment() {
        val json = """
            {
                "caption": "Check out this cool video!",
                "attachment": {
                    "type": "VIDEO",
                    "videoUrl": "https://s3.amazonaws.com/bucket/video.mp4",
                    "thumbnailUrl": "https://s3.amazonaws.com/bucket/thumb.jpg",
                    "width": 1080,
                    "height": 1920,
                    "aspectRatio": 0.5625
                }
            }
        """.trimIndent()

        val request = objectMapper.readValue<CreatePostRequest>(json)
        assertEquals("Check out this cool video!", request.caption)
        assertNotNull(request.attachment)
        assertTrue(request.attachment is VideoAttachmentDto)

        val video = request.attachment as VideoAttachmentDto
        assertEquals(AttachmentType.VIDEO, video.type)
        assertEquals("https://s3.amazonaws.com/bucket/video.mp4", video.videoUrl)
        assertEquals("https://s3.amazonaws.com/bucket/thumb.jpg", video.thumbnailUrl)
        assertEquals(1080, video.width)
        assertEquals(1920, video.height)
        assertEquals(0.5625f, video.aspectRatio)
    }

    @Test
    fun testDeserializeCreatePostRequestWithImageAttachment() {
        val json = """
            {
                "caption": "Photo dump",
                "attachment": {
                    "type": "IMAGE",
                    "images": ["https://s3.amazonaws.com/bucket/img1.jpg", "https://s3.amazonaws.com/bucket/img2.jpg"],
                    "widths": [1080, 720],
                    "heights": [1080, 1280],
                    "aspectRatios": [1.0, 0.5625]
                }
            }
        """.trimIndent()

        val request = objectMapper.readValue<CreatePostRequest>(json)
        assertEquals("Photo dump", request.caption)
        assertNotNull(request.attachment)
        assertTrue(request.attachment is ImageAttachmentDto)

        val image = request.attachment as ImageAttachmentDto
        assertEquals(AttachmentType.IMAGE, image.type)
        assertEquals(2, image.images.size)
        assertEquals(listOf(1080, 720), image.widths)
        assertEquals(listOf(1080, 1280), image.heights)
        assertEquals(listOf(1.0f, 0.5625f), image.aspectRatios)
    }

    @Test
    fun testPostMapperWithVideoPost() {
        val user = UserEntity(
            uid = "user123",
            name = "John Doe",
            image = "https://profile.jpg"
        )
        val post = PostsEntity(
            id = UUID.randomUUID(),
            author = user,
            caption = "Video post",
            postType = PostType.VIDEO,
            likeCount = 10,
            replyCount = 2
        )
        val author = AuthorDetails(
            authorId = "user123",
            authorName = "John Doe",
            authorImage = "https://profile.jpg"
        )
        val media = listOf(
            MediaPost(
                mediaUrl = "https://video.mp4",
                thumbnailUrl = "https://thumb.jpg",
                mediaType = MediaType.VIDEO,
                position = 0,
                width = 1920,
                height = 1080,
                aspectRatio = 1.777f
            )
        )

        val response = mapper.toResponse(post, author, media, isLiked = true)

        assertEquals("Video post", response.caption)
        assertTrue(response.isLiked)
        assertEquals(10, response.likesCount)
        assertEquals(2, response.commentCount)
        assertTrue(response.attachment is VideoAttachmentDto)

        val video = response.attachment as VideoAttachmentDto
        assertEquals(AttachmentType.VIDEO, video.type)
        assertEquals("https://video.mp4", video.videoUrl)
        assertEquals("https://thumb.jpg", video.thumbnailUrl)
        assertEquals(1920, video.width)
        assertEquals(1080, video.height)
        assertEquals(1.777f, video.aspectRatio)

        // Test serialization to JSON
        val json = objectMapper.writeValueAsString(response)
        assertTrue(json.contains("\"type\":\"VIDEO\""))
        assertTrue(json.contains("\"videoUrl\":\"https://video.mp4\""))
        assertTrue(json.contains("\"thumbnailUrl\":\"https://thumb.jpg\""))
    }

    @Test
    fun testPostMapperWithImagePost() {
        val user = UserEntity(
            uid = "user123",
            name = "Jane Doe"
        )
        val post = PostsEntity(
            id = UUID.randomUUID(),
            author = user,
            caption = "Images post",
            postType = PostType.IMAGE
        )
        val author = AuthorDetails(authorId = "user123", authorName = "Jane Doe")
        val media = listOf(
            MediaPost(
                mediaUrl = "https://img1.jpg",
                mediaType = MediaType.IMAGE,
                position = 0,
                width = 800,
                height = 600,
                aspectRatio = 1.333f
            ),
            MediaPost(
                mediaUrl = "https://img2.jpg",
                mediaType = MediaType.IMAGE,
                position = 1,
                width = 800,
                height = 800,
                aspectRatio = 1.0f
            )
        )

        val response = mapper.toResponse(post, author, media, isLiked = false)

        assertTrue(response.attachment is ImageAttachmentDto)
        val img = response.attachment as ImageAttachmentDto
        assertEquals(listOf("https://img1.jpg", "https://img2.jpg"), img.images)
        assertEquals(listOf(800, 800), img.widths)
        assertEquals(listOf(600, 800), img.heights)
        assertEquals(listOf(1.333f, 1.0f), img.aspectRatios)

        // Test serialization to JSON
        val json = objectMapper.writeValueAsString(response)
        assertTrue(json.contains("\"type\":\"IMAGE\""))
        assertTrue(json.contains("\"images\":[\"https://img1.jpg\",\"https://img2.jpg\"]"))
        assertTrue(json.contains("\"aspectRatios\":[1.333,1.0]"))
    }

    @Test
    fun testDeserializeCreatePostRequestWithDocumentAttachment() {
        val json = """
            {
                "caption": "Project specification PDF",
                "attachment": {
                    "type": "DOCUMENT",
                    "url": "https://s3.amazonaws.com/bucket/doc.pdf",
                    "name": "spec.pdf",
                    "size": 1048576
                }
            }
        """.trimIndent()

        val request = objectMapper.readValue<CreatePostRequest>(json)
        assertEquals("Project specification PDF", request.caption)
        assertNotNull(request.attachment)
        assertTrue(request.attachment is DocumentAttachmentDto)

        val doc = request.attachment as DocumentAttachmentDto
        assertEquals(AttachmentType.DOCUMENT, doc.type)
        assertEquals("https://s3.amazonaws.com/bucket/doc.pdf", doc.url)
        assertEquals("spec.pdf", doc.name)
        assertEquals(1048576L, doc.size)
    }

    @Test
    fun testPostMapperWithDocumentPost() {
        val user = UserEntity(
            uid = "user123",
            name = "John Doe"
        )
        val post = PostsEntity(
            id = UUID.randomUUID(),
            author = user,
            caption = "Important Notes",
            postType = PostType.DOCUMENT,
            likeCount = 5,
            replyCount = 1
        )
        val author = AuthorDetails(authorId = "user123", authorName = "John Doe")
        val media = listOf(
            MediaPost(
                mediaUrl = "https://s3.amazonaws.com/bucket/notes.pdf",
                mediaType = MediaType.DOCUMENT,
                position = 0,
                fileName = "notes.pdf",
                fileSize = 2048576L
            )
        )

        val response = mapper.toResponse(post, author, media, isLiked = false)

        assertEquals("Important Notes", response.caption)
        assertTrue(response.attachment is DocumentAttachmentDto)

        val doc = response.attachment as DocumentAttachmentDto
        assertEquals(AttachmentType.DOCUMENT, doc.type)
        assertEquals("https://s3.amazonaws.com/bucket/notes.pdf", doc.url)
        assertEquals("notes.pdf", doc.name)
        assertEquals(2048576L, doc.size)

        // Test serialization to JSON
        val json = objectMapper.writeValueAsString(response)
        assertTrue(json.contains("\"type\":\"DOCUMENT\""))
        assertTrue(json.contains("\"url\":\"https://s3.amazonaws.com/bucket/notes.pdf\""))
        assertTrue(json.contains("\"name\":\"notes.pdf\""))
        assertTrue(json.contains("\"size\":2048576"))
    }
}

