package com.iotabuild.campuscircle.CollabService.Exceptions

class CollabNotFoundException(message: String) : RuntimeException(message)

class UnauthorizedCollabException(message: String) : RuntimeException(message)

class DuplicateCollabRequestException(message: String) : RuntimeException(message)
