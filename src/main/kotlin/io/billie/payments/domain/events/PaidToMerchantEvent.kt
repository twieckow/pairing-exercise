package io.billie.payments.domain.events

import io.billie.common.domain.DomainEvent
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class PaidToMerchantEvent(
        val eventDateTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
        var paymentId: UUID,
        val orderId: UUID,
        val merchantId: UUID,
        val paidAmount: BigDecimal,
) : DomainEvent {
    override fun eventDateTime(): LocalDateTime = eventDateTime
}