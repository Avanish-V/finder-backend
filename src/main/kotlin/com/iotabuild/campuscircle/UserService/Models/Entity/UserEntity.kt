package com.iotabuild.campuscircle.UserService.Models.Entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.iotabuild.campuscircle.Skills.SkillDto
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @Column(name = "id")
    val uid: String = "",   // Firebase UID as PK
    val email: String = "",
    var name: String = "",
    var image: String? = null,
    var summary: String = "",
    var tagline: String = "",
    var phoneNumber: String = "",

    @Enumerated(EnumType.STRING)
    var gender: Gender = Gender.UNSPECIFIED,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    var education: EducationEntity? = null,

    @Column(name = "is_verified")
    var isVerified: Boolean = false,

    @Column(name = "github_username", unique = true)
    var githubUsername: String? = null,

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    var skills: List<SkillDto> = mutableListOf(),

    @Column(name = "fcm_token")
    var fcmToken: String? = null,

    /** Total aura points accumulated by the user. */
    @Column(name = "aura_points", nullable = false)
    var auraPoints: Int = 0

)

enum class Gender { MALE, FEMALE, OTHER, UNSPECIFIED }
