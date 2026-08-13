package com.iotabuild.campuscircle.UserService.Repository

import com.iotabuild.campuscircle.UserService.Models.Entity.EducationEntity
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {
    fun findByGithubUsernameIgnoreCase(githubUsername: String): UserEntity?
    fun findAllByUidIn(uids: Collection<String>): List<UserEntity>
}

@Repository
interface CampusRepository : JpaRepository<EducationEntity, Long> {
    fun findByUser(user: UserEntity): EducationEntity?
}
