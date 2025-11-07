package com.iotabuild.campuscircle.UserService.Service

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.UserService.Models.DTOs.UpdateCampusRequest
import com.iotabuild.campuscircle.UserService.Models.DTOs.UpdateProfileRequest
import com.iotabuild.campuscircle.UserService.Models.DTOs.UserProfileResponse
import com.iotabuild.campuscircle.UserService.Models.Entity.CampusEntity
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.CampusRepository
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val campusRepository: CampusRepository,
) {

    fun getOrCreateUser(firebaseToken: FirebaseToken): UserEntity {
        val uid = firebaseToken.uid
        return userRepository.findById(uid).orElseGet {
            val newUser = UserEntity(
                uid = uid,
                email = firebaseToken.email ?: "",
                image = firebaseToken.picture.toString(),
                name = firebaseToken.name.toString(),
            )
            userRepository.save(newUser)
        }
    }


    @Transactional
    fun updateProfile(firebaseToken: FirebaseToken, request: UpdateProfileRequest): UserEntity {
        val user = getOrCreateUser(firebaseToken)
        request.updateUserName?.let { user.name = it }
        request.updateProfileImage?.let { user.image = it }
        request.updateAbout?.let { user.about = it }
        request.updateGender?.let { user.gender = it }
        request.updateTagline?.let { user.tagline = it }
        return userRepository.save(user)
    }


    @Transactional
    fun updateCampus(firebaseToken: FirebaseToken, dto: UpdateCampusRequest): CampusEntity {
        val user = getOrCreateUser(firebaseToken)

        // ✅ Correct way: fetch by user (OneToOne relationship)
        val campus = campusRepository.findByUser(user)
            ?: CampusEntity(user = user) // create new if none exists

        // ✅ Update fields
        dto.collegeName?.let { campus.collegeName = it }
        dto.university?.let { campus.university = it }
        dto.logo?.let { campus.logo = it }
        dto.fieldOfStudy?.let { campus.fieldOfStudy = it }
        dto.code?.let { campus.code = it }
        dto.degree?.let { campus.degree = it }
        dto.courseStart?.let { campus.courseStart = it }
        dto.startTimestamp?.let { campus.startTimestamp = it }
        dto.courseEnd?.let { campus.courseEnd = it }
        dto.endTimestamp?.let { campus.endTimestamp = it }
        campus.isCurrent = dto.isCurrent

        // ✅ Hibernate does UPDATE if campus already has an ID, INSERT if it’s new
        return campusRepository.save(campus)
    }


    fun getUserById(userId: String): UserProfileResponse?{

        val profile = userRepository.findById(userId)

        val mapData = profile.map { user->
             UserProfileResponse(
                uid = user.uid,
                name = user.name,
                email = user.email,
                image =user.image,
                about = user.about,
                tagline = user.tagline,
                gender = user.gender,
                lastLogin = user.lastLogin,
                campus = UpdateCampusRequest(
                    collegeName = user.campus?.collegeName,
                    university = user.campus?.university,
                    logo = user.campus?.logo,
                    fieldOfStudy = user.campus?.fieldOfStudy,
                    code = user.campus?.code,
                    degree = user.campus?.degree,
                    courseStart = user.campus?.courseStart,
                    startTimestamp = user.campus?.startTimestamp,
                    courseEnd = user.campus?.courseStart,
                    endTimestamp = user.campus?.startTimestamp,
                    isCurrent = user.campus?.isCurrent ?: false
                ),
                followersCount = 0,
                connectionsCount = 0
            )
        }

        return mapData.orElse(null)

    }


}

