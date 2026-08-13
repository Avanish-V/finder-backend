package com.iotabuild.campuscircle.UserService.Service

import com.iotabuild.campuscircle.Common.exception.UserNotFoundException
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserQueryService(
    private val userRepository: UserRepository
) {
    fun getUserOrThrow(userId: String): UserEntity {
        return userRepository.findById(userId)
            .orElseThrow {
                UserNotFoundException(userId)
            }
    }

}