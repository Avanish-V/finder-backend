package com.iotabuild.campuscircle.AuraFeature.mapper

import com.iotabuild.campuscircle.AuraFeature.Entity.AuraTransactionEntity
import com.iotabuild.campuscircle.AuraFeature.dto.AuraTransactionResponse

fun AuraTransactionEntity.toResponse() =
    AuraTransactionResponse(
        id = requireNotNull(id),
        userId = userId,
        amount = amount,
        type = type,
        referenceId = referenceId,
        createdAt = createdAt
    )