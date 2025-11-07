package com.iotabuild.campuscircle.UserService.Controller

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.UserService.Models.DTOs.UpdateCampusRequest
import com.iotabuild.campuscircle.UserService.Models.DTOs.UpdateInterestsRequest
import com.iotabuild.campuscircle.UserService.Models.DTOs.UpdateProfileRequest
import com.iotabuild.campuscircle.UserService.Models.DTOs.UserProfileResponse
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.function.EntityResponse

@RestController
@RequestMapping("/users")
class UserController(private val userService: com.iotabuild.campuscircle.UserService.Service.UserService) {

    @GetMapping("/me")
    fun getMe(request: HttpServletRequest): UserEntity {
        val token = request.getAttribute("firebaseUser") as FirebaseToken
        return userService.getOrCreateUser(token)
    }

    @PatchMapping("/me/profile")
    fun updateProfile(request: HttpServletRequest, @RequestBody body: UpdateProfileRequest) =
        userService.updateProfile(request.getAttribute("firebaseUser") as FirebaseToken, body)

    @PatchMapping("/me/campus")
    fun updateCampus(request: HttpServletRequest, @RequestBody body: UpdateCampusRequest) =
        userService.updateCampus(request.getAttribute("firebaseUser") as FirebaseToken, body)

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String): ResponseEntity<UserProfileResponse>{
        val user = userService.getUserById(userId)
        return ResponseEntity.ok(user)
    }


}
