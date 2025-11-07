package com.iotabuild.campuscircle.UserService.Models.Entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    val uid: String = "",   // Firebase UID as PK
    val email: String = "",
    var name: String = "",
    var image: String?=null,
    var about: String = "",
    var tagline: String = "",

    var lastLogin: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    var gender: Gender = Gender.UNSPECIFIED,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    var campus: CampusEntity? = null

) {
    // 👇 Required by JPA
    protected constructor() : this("", "",)
}



enum class Gender { MALE, FEMALE, OTHER, UNSPECIFIED }