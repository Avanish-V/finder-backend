package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class MatchPreferenceResponse(
    val id: Long,
    val code: String,
    val title: String,
    val description: String?,
    val icon: String?,
    val active: Boolean,
)
