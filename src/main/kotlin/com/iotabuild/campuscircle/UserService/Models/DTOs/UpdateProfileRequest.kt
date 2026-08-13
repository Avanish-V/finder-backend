package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.iotabuild.campuscircle.UserService.Models.Entity.Gender

data class BasicDetailsRequest(
    val name: String,
    val photo: String? = null,
    val tagline: String,
    val gender: Gender
)

data class SummaryRequest(
    val summary: String
)
