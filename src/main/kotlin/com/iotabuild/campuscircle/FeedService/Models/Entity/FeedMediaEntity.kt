package com.iotabuild.campuscircle.FeedService.Models.Entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "feed_media")
class FeedMediaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    var post: PostsEntity? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var mediaUrl: String = "",

    @Column(nullable = true, columnDefinition = "TEXT")
    var thumbnailUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var mediaType: MediaType = MediaType.IMAGE,

    @Column(nullable = false)
    var position: Int = 0,

    @Column(nullable = true)
    var width: Int? = null,

    @Column(nullable = true)
    var height: Int? = null,

    @Column(nullable = true)
    var aspectRatio: Float? = null,

    @Column(nullable = true)
    var fileName: String? = null,

    @Column(nullable = true)
    var fileSize: Long? = null
)

