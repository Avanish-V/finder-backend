package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.iotabuild.campuscircle.UserService.Models.Entity.Gender


data class UpdateProfileRequest(
    val updateUserName: String? = null,
    val updateProfileImage: String? = null,
    val updateAbout: String? = null,
    val updateGender: Gender? = null,
    val updateTagline: String?=null
)

data class UpdateCampusRequest(
    val collegeName: String? = null,
    val university: String? = null,
    val logo: String? = null,
    val fieldOfStudy: String? = null,
    val code: String? = null,
    val degree: String? = null,
    val courseStart: String? = null,
    val startTimestamp: Long? = null,
    val courseEnd: String? = null,
    val endTimestamp: Long? = null,
    val isCurrent: Boolean = false,
)

data class UpdateInterestsRequest(
    val interests: List<String>
)

