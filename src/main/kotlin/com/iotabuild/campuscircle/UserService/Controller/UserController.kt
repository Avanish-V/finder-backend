package com.iotabuild.campuscircle.UserService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.Skills.SkillDto
import com.iotabuild.campuscircle.UserService.Models.DTOs.*
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Service.UserQueryService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: com.iotabuild.campuscircle.UserService.Service.UserService,
    private val userQueryService: UserQueryService
) {

    @GetMapping("/me")
    fun getMe(request: HttpServletRequest): ResponseEntity<UserProfileResponse> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val user = userService.getOrCreateUser(token)
        val profile = userService.getUserById(user.uid, user.uid)
        return ResponseEntity.ok(profile)
    }

    @PatchMapping("/me/profile")
    fun updateProfile(
        request: HttpServletRequest,
        @RequestBody body: BasicDetailsRequest
    ): ResponseEntity<UserProfileResponse> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val user = userService.updateProfile(token, body)
        val profile = userService.getUserById(user.uid, user.uid)
        return ResponseEntity.ok(profile)
    }

    @PatchMapping("/me/summary")
    fun updateSummary(
        request: HttpServletRequest,
        @RequestBody body: String
    ): ResponseEntity<UserProfileResponse> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val user = userService.updateSummary(token, body)
        val profile = userService.getUserById(user.uid, user.uid)
        return ResponseEntity.ok(profile)
    }

    @PatchMapping("/me/campus")
    fun updateCampus(
        request: HttpServletRequest,
        @RequestBody body: Education
    ): ResponseEntity<UserProfileResponse> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        val campus = userService.updateCampus(token, body)
        val profile = userService.getUserById(token.uid, token.uid)
        return ResponseEntity.ok(profile)
    }

    @PatchMapping("/me/skills")
    fun updateSkills(
        request: HttpServletRequest,
        @RequestBody body: List<SkillDto>
    ): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(mapOf("error" to "Skills can only be updated/verified via GitHub profile analysis for skill fairness."))
    }

    @PostMapping("/me/github")
    fun updateSkillsFromGitHub(
        request: HttpServletRequest,
        @RequestBody body: GitHubUsernameRequest
    ): ResponseEntity<Any> {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        try {
            val user = userService.updateSkillsFromGitHub(token, body.githubUsername)
            val profile = userService.getUserById(user.uid, user.uid) ?: return ResponseEntity.notFound().build()
            return ResponseEntity.ok(profile)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Failed to verify GitHub username")))
        }
    }


    @GetMapping("/{userId}")
    fun getUserById(
        request: HttpServletRequest,
        @PathVariable userId: String
    ): ResponseEntity<UserProfileResponse> {
        val token = request.getAttribute("firebaseUser") as? FirebaseToken
        val profile = userService.getUserById(userId, token?.uid)
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/view/{userId}")
    fun getUserForRecruiter(
        @PathVariable userId: String
    ): ResponseEntity<UserProfileResponse> {
        val profile = userService.getUserById(userId)
        return ResponseEntity.ok(profile)
    }


    @PatchMapping("/me/fcm-token")
    fun updateToken(
        @AuthenticationPrincipal uid: String,
        @RequestBody request: String
    ) {

        userService.updateFcmToken(uid, request)
    }
}

data class GitHubUsernameRequest(val githubUsername: String)
