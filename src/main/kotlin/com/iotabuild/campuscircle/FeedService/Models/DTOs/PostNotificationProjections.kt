package com.iotabuild.campuscircle.FeedService.Models.DTOs

import java.util.UUID

interface PostNotificationSummaryProjection {
    fun getId(): UUID
    fun getCaption(): String
    fun getThumbnailUrl(): String?
    fun getMediaUrl(): String?
}

interface UpvoterWithPostProjection {
    fun getPostId(): UUID
    fun getUid(): String
    fun getName(): String
    fun getImage(): String?
    fun getAbout(): String?
    fun getTagline(): String?
}
