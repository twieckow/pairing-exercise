package io.billie.orders.domain.event

import io.billie.common.domain.DomainEvent
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class OrderShippedEvent(
        val eventDateTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
        val orderId: UUID,
        val merchantId: UUID,
        val shippedAmount: BigDecimal
) : DomainEvent {
    override fun eventDateTime(): LocalDateTime = eventDateTime
}
