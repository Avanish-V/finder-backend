package com.iotabuild.campuscircle.UserService.Models.DTOs

import com.iotabuild.campuscircle.Skills.SkillDto
import com.iotabuild.campuscircle.UserService.Models.Entity.Gender

data class BaseProfile(
    val name: String = "",
    val image: String? = null,
    val summary: String = "",
    val tagline: String = "",
    val gender: Gender = Gender.UNSPECIFIED
)

data class Contact(
    val email: String = "",
    val phoneNumber: String = ""
)

data class Education(
    val college: String,
    val course: String,
    val specialization: String,
    val cgpa: String,
    val start: String,
    val end: String
)

/** Aura points + derived level — no streak data. */
data class AuraInfo(
    val auraPoints: Int = 0,
    val level: AuraLevel = AuraLevel.NEWCOMER
)

enum class AuraLevel(val label: String, val minPoints: Int) {
    NEWCOMER("Newcomer",      0),
    SPARK("Spark",           100),
    RISING("Rising",         300),
    GLOWING("Glowing",       700),
    RADIANT("Radiant",      1500),
    BLAZING("Blazing",      3000),
    LEGENDARY("Legendary", 6000);

    companion object {
        fun fromPoints(points: Int): AuraLevel =
            entries.sortedByDescending { it.minPoints }.first { points >= it.minPoints }
    }
}

data class UserProfileResponse(
    val uid: String,
    val baseProfile: BaseProfile,
    val contact: Contact,
    val education: Education?,
    val skills: List<SkillDto>,
    val summary: String?,
    val isCurrentUser: Boolean,
    val githubUsername: String?,
    val aura: AuraInfo = AuraInfo()
)
