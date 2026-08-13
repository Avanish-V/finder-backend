package com.iotabuild.campuscircle.Common.exception

class UserNotFoundException(
    userId: String
) : RuntimeException("User not found with id: $userId")