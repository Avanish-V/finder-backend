package com.iotabuild.campuscircle.UserService.Repository

import com.iotabuild.campuscircle.UserService.Models.Entity.MatchPreferenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchPreferenceRepository : JpaRepository<MatchPreferenceEntity, Long> {
    fun findAllByActiveTrueOrderByIdAsc(): List<MatchPreferenceEntity>
    fun findAllByOrderByIdAsc(): List<MatchPreferenceEntity>
    fun findByCode(code: String): MatchPreferenceEntity?
}
