package com.iotabuild.campuscircle.Common.exception

import java.util.UUID

class ReplyNotFoundException(
    replyId: UUID
) : RuntimeException(
    "Reply not found: $replyId"
)