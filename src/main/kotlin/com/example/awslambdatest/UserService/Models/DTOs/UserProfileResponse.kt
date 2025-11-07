package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.iotabuild.campuscircle.UserService.Models.Entity.Gender
import java.time.LocalDateTime

data class UserProfileResponse(
    val uid: String,
    val name: String,
    val email: String,
    val image: String?,
    val about: String?,
    val tagline: String?,
    val gender: Gender,
    val lastLogin: LocalDateTime,
    val campus: UpdateCampusRequest?,
    val followersCount: Long,
    val connectionsCount: Long
)

