package com.iotabuild.campuscircle.Skills.Controller

import com.iotabuild.campuscircle.Skills.Service.SkillsService
import com.iotabuild.campuscircle.Skills.SkillDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/v1/skills")
class SkillController(private val skillsService: SkillsService) {

    @GetMapping("/{query}")
    fun findSkill(@PathVariable query: String): ResponseEntity<List<SkillDto>>{
        val data =  skillsService.findSkillByName(query)
        return ResponseEntity.ok(data)
    }

    @GetMapping("/search")
    fun searchSkills(@RequestParam query: String): ResponseEntity<List<SkillDto>> {
        val data = skillsService.findSkillByName(query)
        return ResponseEntity.ok(data)
    }
}