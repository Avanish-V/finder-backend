package com.iotabuild.campuscircle.Skills.Repository

import com.iotabuild.campuscircle.Skills.Entity.SkillEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository: JpaRepository<SkillEntity, Long>{
    fun findTop20ByNameContainingIgnoreCase(query: String): List<SkillEntity>
}