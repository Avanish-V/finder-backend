package com.iotabuild.campuscircle.Skills.Service

import com.iotabuild.campuscircle.Skills.Repository.SkillRepository
import com.iotabuild.campuscircle.Skills.SkillDto
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class SkillsService (private val skillRepository: SkillRepository){
    fun findSkillByName(query: String): List<SkillDto>{
        println("Searching for---------------------------------: $query")
        return skillRepository.findTop20ByNameContainingIgnoreCase(query)
            .map {
                SkillDto(
                    id = it.id.toString(),
                    name = it.name,
                    category = it.category
                )
            }.toList()

    }
}