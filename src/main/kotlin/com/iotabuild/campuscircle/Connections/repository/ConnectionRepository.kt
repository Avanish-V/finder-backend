package com.iotabuild.campuscircle.Connections.repository


import com.iotabuild.campuscircle.Connections.controller.ConnectionController
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionRequestResponse
import com.iotabuild.campuscircle.Connections.models.dto.ConnectionResponse
import com.iotabuild.campuscircle.Connections.models.entity.Connection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ConnectionRepository : JpaRepository<Connection, UUID> {

    @Query("""
    SELECT c
    FROM Connection c
    WHERE (c.userA.uid = :a AND c.userB.uid = :b)
       OR (c.userA.uid = :b AND c.userB.uid = :a)
""")
    fun existsBetween(@Param("a") a: String, @Param("b") b: String): Connection?



    @Query("""
        SELECT new com.iotabuild.campuscircle.Connections.models.dto.ConnectionResponse(
            c.id,
            CASE WHEN c.userA.uid = :uid THEN c.userB.uid ELSE c.userA.uid END,
            CASE WHEN c.userA.uid = :uid THEN c.userB.name ELSE c.userA.name END,
            CASE WHEN c.userA.uid = :uid THEN c.userB.image ELSE c.userA.image END,
            CASE WHEN c.userA.uid = :uid THEN c.userB.tagline ELSE c.userA.tagline END
        )
        FROM Connection c
        WHERE c.userA.uid = :uid OR c.userB.uid = :uid
        ORDER BY c.connectedAt DESC
    """)
    fun findAllForUser(
        @Param("uid") uid: String,
        pageable: Pageable
    ): Page<ConnectionResponse>

    @Query("""
        SELECT COUNT(c)
        FROM Connection c
        WHERE c.userA.uid = :uid OR c.userB.uid = :uid
    """)
    fun countConnectionsForUser(@Param("uid") uid: String): Long

}