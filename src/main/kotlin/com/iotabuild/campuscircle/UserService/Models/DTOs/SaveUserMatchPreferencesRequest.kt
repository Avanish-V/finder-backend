package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

data class SaveUserMatchPreferencesRequest(
    @JsonProperty("preferenceIds")
    @JsonAlias("preference_ids")
    val preferenceIds: List<Long> = emptyList()
)
