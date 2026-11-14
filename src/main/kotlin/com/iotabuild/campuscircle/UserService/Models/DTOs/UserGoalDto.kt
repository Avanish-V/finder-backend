package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.fasterxml.jackson.annotation.JsonProperty

data class UserGoalDto(
    val idx: Int = 0,
    val id: Long = 1,
    val code: String = "CO_BUILDER",
    val title: String = "Co-builder / Teammate",
    val description: String = "Find someone to build and work on projects together.",
    val icon: String = "groups",
    val active: Boolean = true,
    @JsonProperty("created_at")
    val createdAt: String = "2026-08-30 18:52:10.40401+00"
)
