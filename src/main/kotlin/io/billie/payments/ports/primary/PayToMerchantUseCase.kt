package io.billie.payments.ports.primary

import io.billie.common.infrastructure.DomainEventPublisher
import io.billie.payments.domain.Payment
import io.billie.payments.domain.events.PaidToMerchantEvent
import io.billie.payments.domain.exceptions.PaymentValidationException
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class PayToMerchantUseCase(private var domainEventPublisher: DomainEventPublisher) {

    data class Input(val orderId: UUID, val merchantId: UUID, val shippedAmount: BigDecimal)

    fun execute(input: Input) {
        if (input.shippedAmount <= BigDecimal.ZERO) throw PaymentValidationException("Payment amount must be greater than zero")

        val payment = Payment.makePaymentToMerchant(input.shippedAmount, input.merchantId, input.orderId)

        domainEventPublisher.publish(PaidToMerchantEvent(
                paymentId = payment.id,
                orderId = payment.orderId,
                merchantId = payment.merchantId,
                paidAmount = payment.paymentAmount
        ))
    }
}