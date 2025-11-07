package com.iotabuild.campuscircle.UserService.Repository

import com.iotabuild.campuscircle.UserService.Models.Entity.CampusEntity
import com.iotabuild.campuscircle.UserService.Models.Entity.Interest

import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, String>

@Repository
interface CampusRepository : JpaRepository<CampusEntity, Long> {
    fun findByUser(user: UserEntity): CampusEntity?
}


