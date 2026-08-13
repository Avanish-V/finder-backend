package com.iotabuild.campuscircle.Notification.controller

import com.iotabuild.campuscircle.Notification.service.NotificationCommandService
import com.iotabuild.campuscircle.Notification.service.NotificationQueryService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications")
class NotificationController(

    private val commandService: NotificationCommandService,

    private val queryService: NotificationQueryService

) {

    @GetMapping
    fun notifications(
        @AuthenticationPrincipal uid: String,
        @RequestParam page: Int,
        @RequestParam size: Int
    ) = queryService.getNotifications(uid, page, size)

    @GetMapping("/unread-count")
    fun unread(@AuthenticationPrincipal uid: String) = queryService.unreadCount(uid)

    @PatchMapping("/{id}/read")
    fun read(@AuthenticationPrincipal uid: String, @PathVariable id: Long) { commandService.markRead(uid, id) }

    @PatchMapping("/read-all")
    fun readAll(@AuthenticationPrincipal uid: String) { commandService.markAllRead(uid) }

    @GetMapping("/{id}")
    fun getNotification(
        @AuthenticationPrincipal uid: String,
        @PathVariable id: Long
    ) = queryService.getNotification(uid, id)

}