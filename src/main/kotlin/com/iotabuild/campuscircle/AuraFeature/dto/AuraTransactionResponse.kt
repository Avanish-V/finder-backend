package com.iotabuild.campuscircle.AuraFeature.dto

import java.time.Instant
import java.util.UUID

data class AuraTransactionResponse(
    val id: UUID,
    val userId: String,
    val amount: Int,
    val type: AuraTransactionType,
    val referenceId: String?,
    val createdAt: Instant
)