package com.iotabuild.campuscircle.UserSearch.domain.dto

import java.util.UUID

data class SearchResponse(
    val uid: String,
    val name: String,
    val image: String?=null,
    val tagline: String,
    val about: String
)