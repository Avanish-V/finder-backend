package com.iotabuild.campuscircle.Common.exception

class PostNotFoundException(
    postId: Long
) : RuntimeException("Post not found with id: $postId")