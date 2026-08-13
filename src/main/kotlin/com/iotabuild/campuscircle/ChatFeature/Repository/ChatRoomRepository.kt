package com.iotabuild.campuscircle.ChatFeature.Repository

import com.iotabuild.campuscircle.ChatFeature.Models.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ChatRoomRepository : JpaRepository<ChatRoomEntity, UUID>
