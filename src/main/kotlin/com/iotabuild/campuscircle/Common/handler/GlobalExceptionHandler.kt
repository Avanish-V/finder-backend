package com.iotabuild.campuscircle.Common.handler

import com.iotabuild.campuscircle.CollabService.Exceptions.CollabNotFoundException
import com.iotabuild.campuscircle.CollabService.Exceptions.DuplicateCollabRequestException
import com.iotabuild.campuscircle.CollabService.Exceptions.UnauthorizedCollabException
import com.iotabuild.campuscircle.Common.exception.NotificationNotFoundException
import com.iotabuild.campuscircle.Common.exception.PostNotFoundException
import com.iotabuild.campuscircle.Common.exception.UserNotFoundException
import com.iotabuild.campuscircle.Common.response.ApiError
import com.iotabuild.campuscircle.FeedService.exceptions.DuplicateLikeException
import com.iotabuild.campuscircle.FeedService.exceptions.LikeNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(
        ex: UserNotFoundException
    ): ResponseEntity<ApiError> {

        val error = ApiError(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "User not found",
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(error)
    }

    @ExceptionHandler(PostNotFoundException::class)
    fun handlePostNotFound(
        ex: PostNotFoundException
    ): ResponseEntity<ApiError> {

        val error = ApiError(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "Post not found",
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(error)
    }

    @ExceptionHandler(NotificationNotFoundException::class)
    fun handleNotificationNotFound(

        ex: NotificationNotFoundException,

        request: HttpServletRequest

    ): ResponseEntity<ApiError> {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(

                ApiError(

                    status = HttpStatus.NOT_FOUND.value(),

                    error = HttpStatus.NOT_FOUND.reasonPhrase,

                    message = ex.message ?: "Notification not found",
                )
            )
    }

    @ExceptionHandler(com.iotabuild.campuscircle.FeedService.exceptions.PostNotFoundException::class)
    fun handlePostNotFound(ex: com.iotabuild.campuscircle.FeedService.exceptions.PostNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = "Not Found",
                message = ex.message ?: "Post not found",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(DuplicateLikeException::class)
    fun handleDuplicateLike(ex: DuplicateLikeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(
                status = HttpStatus.CONFLICT.value(),
                error = "Conflict",
                message = ex.message ?: "Post already liked",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(LikeNotFoundException::class)
    fun handleLikeNotFound(ex: LikeNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = "Not Found",
                message = ex.message ?: "Like not found",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(CollabNotFoundException::class)
    fun handleCollabNotFound(ex: CollabNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = "Not Found",
                message = ex.message ?: "Collaboration not found",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(UnauthorizedCollabException::class)
    fun handleUnauthorizedCollab(ex: UnauthorizedCollabException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(
                status = HttpStatus.FORBIDDEN.value(),
                error = "Forbidden",
                message = ex.message ?: "Access Denied",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(DuplicateCollabRequestException::class)
    fun handleDuplicateCollabRequest(ex: DuplicateCollabRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(
                status = HttpStatus.CONFLICT.value(),
                error = "Conflict",
                message = ex.message ?: "Collaboration request already exists",
                timestamp = Instant.now()
            ))
    }
}

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant
)