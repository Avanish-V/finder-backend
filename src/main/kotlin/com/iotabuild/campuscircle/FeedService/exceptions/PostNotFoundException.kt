package com.iotabuild.campuscircle.FeedService.exceptions

class PostNotFoundException(message: String) : RuntimeException(message)
class DuplicateLikeException(message: String) : RuntimeException(message)
class LikeNotFoundException(message: String) : RuntimeException(message)