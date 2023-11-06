package io.billie.orders.domain.event

import io.billie.common.domain.DomainEvent
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class OrderCreatedEvent(
        val eventDateTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
        val orderId: UUID
) : DomainEvent {
    override fun eventDateTime(): LocalDateTime = eventDateTime
}
