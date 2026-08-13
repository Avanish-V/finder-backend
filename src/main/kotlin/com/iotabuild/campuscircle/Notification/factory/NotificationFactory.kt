package com.iotabuild.campuscircle.Notification.factory

import com.iotabuild.campuscircle.Notification.event.NotificationEvent
import com.iotabuild.campuscircle.Notification.strategy.NotificationStrategy
import org.springframework.stereotype.Component

@Component
class NotificationFactory(

    private val strategies: List<NotificationStrategy>

) {

    fun getStrategy(
        event: NotificationEvent
    ): NotificationStrategy {

        return strategies.firstOrNull {

            it.supports(event)

        } ?: throw IllegalArgumentException(

            "No strategy found for ${event::class.simpleName}"
        )
    }
}