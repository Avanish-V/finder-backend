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
@Table(name = "campuses")
data class CampusEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var collegeName: String? = null,
    var university: String? = null,
    var logo: String? = null,
    var fieldOfStudy: String? = null,
    var code: String? = null,
    var degree: String? = null,
    var courseStart: String? = null,
    var startTimestamp: Long? = null,
    var courseEnd: String? = null,
    var endTimestamp: Long? = null,
    var isCurrent: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid")
    @JsonBackReference
    var user: UserEntity? = null
)

