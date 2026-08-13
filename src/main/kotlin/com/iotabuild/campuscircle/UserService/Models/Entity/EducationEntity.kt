package com.iotabuild.campuscircle.UserService.Models.Entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "education")
data class EducationEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var college: String? = null,
    var specialization: String? = null,
    var course: String? = null,
    var courseEnd: String? = null,
    var courseStart: String? = null,
    var cgpa: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid")
    @JsonBackReference
    var user: UserEntity? = null
)

