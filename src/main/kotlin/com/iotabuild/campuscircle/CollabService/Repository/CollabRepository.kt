package com.iotabuild.campuscircle.CollabService.Repository

import com.iotabuild.campuscircle.CollabService.Models.CollabEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CollabRepository : JpaRepository<CollabEntity, Long> {

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<CollabEntity>
    fun findByCollabTypeOrderByCreatedAtDesc(collabType: String, pageable: Pageable): Page<CollabEntity>
    fun findAllByAuthorUidOrderByCreatedAtDesc(authorUid: String, pageable: Pageable): Page<CollabEntity>
}
