package com.iotabuild.campuscircle.UserService.Repository

import com.iotabuild.campuscircle.UserService.Models.Entity.UserMatchPreferenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserMatchPreferenceRepository : JpaRepository<UserMatchPreferenceEntity, Long> {
    fun findAllByUserId(userId: String): List<UserMatchPreferenceEntity>

    @Modifying
    @Transactional
    @Query("DELETE FROM UserMatchPreferenceEntity u WHERE u.userId = :userId")
    fun deleteAllByUserId(userId: String)
}
