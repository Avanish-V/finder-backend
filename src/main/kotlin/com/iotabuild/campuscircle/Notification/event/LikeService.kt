package com.iotabuild.campuscircle.Notification.event

import com.iotabuild.campuscircle.FeedService.Repository.PostLikeRepository
import com.iotabuild.campuscircle.FeedService.Repository.PostsRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class LikeService(

    private val likeRepository: PostLikeRepository,

    private val postRepository: PostsRepository,

    private val publisher: ApplicationEventPublisher
)