package com.iotabuild.campuscircle.UserService.Service

import com.google.firebase.auth.FirebaseToken
import com.iotabuild.campuscircle.Connections.repository.ConnectionRepository
import com.iotabuild.campuscircle.Skills.SkillDto
import com.iotabuild.campuscircle.UserService.Models.DTOs.*
import com.iotabuild.campuscircle.UserService.Models.Entity.EducationEntity
import com.iotabuild.campuscircle.UserService.Models.Entity.UserEntity
import com.iotabuild.campuscircle.UserService.Repository.CampusRepository
import com.iotabuild.campuscircle.UserService.Repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val campusRepository: CampusRepository,
    private val connectionRepository: ConnectionRepository,
    private val userQueryService: UserQueryService
) {

    @Transactional
    fun getOrCreateUser(firebaseToken: FirebaseToken): UserEntity {                
        return userRepository.findById(firebaseToken.uid).orElseGet {
            val newUser = UserEntity(
                uid = firebaseToken.uid,
                email = firebaseToken.email ?: "",
                image = firebaseToken.picture,
                name = firebaseToken.name ?: ""
            )
            userRepository.save(newUser)
        }
    }

    @Transactional
    fun updateFcmToken(
        uid: String,
        token: String

    ) : UserEntity{
        val user = userQueryService.getUserOrThrow(uid)
        user.fcmToken = token
        return user
    }

    @Transactional
    fun updateProfile(firebaseToken: FirebaseToken, request: BasicDetailsRequest): UserEntity {
        val user = getOrCreateUser(firebaseToken)
        user.name = request.name
        user.image = request.photo
        user.tagline = request.tagline
        user.gender = request.gender
        return userRepository.save(user)
    }

    @Transactional
    fun updateSummary(firebaseToken: FirebaseToken, summary: String): UserEntity {
        val user = getOrCreateUser(firebaseToken)
        user.summary = summary
        return userRepository.save(user)
    }

    @Transactional
    fun updateCampus(firebaseToken: FirebaseToken, dto: Education): EducationEntity {
        val user = getOrCreateUser(firebaseToken)

        // ✅ Correct way: fetch by user (OneToOne relationship)
        val campus = campusRepository.findByUser(user)
            ?: EducationEntity(user = user) // create new if none exists

        // ✅ Update fields mapping from mobile Education DTO
        campus.college = dto.college
        campus.course = dto.course
        campus.specialization = dto.specialization
        campus.cgpa = dto.cgpa
        campus.courseEnd = dto.start
        campus.courseStart = dto.end

        return campusRepository.save(campus)
    }

    @Transactional
    fun updateSkills(firebaseToken: FirebaseToken, skills: List<SkillDto>): UserEntity {
        val user = getOrCreateUser(firebaseToken)
        user.skills = skills
        return userRepository.save(user)
    }

    fun getUserById(userId: String, currentUid: String? = null): UserProfileResponse? {
        val user = userRepository.findById(userId).orElse(null) ?: return null
        val isCurrentUser = (currentUid != null && currentUid == userId)

        return UserProfileResponse(
            uid = user.uid,
            baseProfile = BaseProfile(
                name = user.name,
                image = user.image,
                summary = user.summary,
                tagline = user.tagline,
                gender = user.gender
            ),
            contact = Contact(
                email = user.email,
                phoneNumber = user.phoneNumber
            ),
            education = user.education?.let { campus ->
                Education(
                    college = campus.college ?: "",
                    course = campus.course ?: "",
                    specialization = campus.specialization ?: "",
                    cgpa = campus.cgpa ?: "",
                    start = campus.courseEnd ?: "",
                    end = campus.courseStart ?: ""
                )
            },
            skills = user.skills,
            summary = user.summary,
            isCurrentUser = isCurrentUser,
            githubUsername = user.githubUsername,
            aura = AuraInfo(
                auraPoints = user.auraPoints,
                level      = AuraLevel.fromPoints(user.auraPoints)
            )
        )
    }

    @Transactional
    fun updateSkillsFromGitHub(firebaseToken: FirebaseToken, githubUsername: String): UserEntity {
        val user = getOrCreateUser(firebaseToken)

        // Check if username is already linked to another user
        val existingUser = userRepository.findByGithubUsernameIgnoreCase(githubUsername)
        if (existingUser != null && existingUser.uid != user.uid) {
            throw IllegalArgumentException("This GitHub account is already linked to another profile.")
        }
        
        val verifyUid = firebaseToken.uid
        val skillsAnalyserUrl = "http://localhost:8081/api/v1/skills/analyze?username=$githubUsername&verifyUid=$verifyUid"
        val restClient = org.springframework.web.client.RestClient.create()
        val typeRef = object : org.springframework.core.ParameterizedTypeReference<List<SkillDto>>() {}
        
        val verifiedSkills = try {
            restClient.get()
                .uri(skillsAnalyserUrl)
                .retrieve()
                .body(typeRef) ?: emptyList()
        } catch (e: Exception) {
            if (e is org.springframework.web.client.RestClientResponseException) {
                val errorBody = e.responseBodyAsString
                val map = try {
                    val mapper = com.fasterxml.jackson.databind.ObjectMapper()
                    mapper.readValue(errorBody, Map::class.java)
                } catch (ex: Exception) {
                    null
                }
                val message = map?.get("message") as? String ?: errorBody
                throw IllegalArgumentException(message)
            }
            throw IllegalArgumentException("Failed to analyze GitHub profile: ${e.message}")
        }
        
        user.githubUsername = githubUsername
        user.skills = verifiedSkills
        return userRepository.save(user)
    }
}
