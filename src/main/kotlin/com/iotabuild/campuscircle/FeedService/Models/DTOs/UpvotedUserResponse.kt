package com.iotabuild.campuscircle.FeedService.Models.DTOs

data class UpvotedUserResponse(
    val uid: String,
    val name: String,
    val image: String? = null,
    val about: String? = null,
    val tagline: String? = null
)
