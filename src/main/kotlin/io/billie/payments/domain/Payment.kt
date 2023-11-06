package io.billie.payments.domain

import io.billie.payments.domain.exceptions.PaymentValidationException
import java.math.BigDecimal
import java.util.*

class Payment(
        val id: UUID,
        val paymentAmount: BigDecimal,
        val merchantId: UUID,
        val orderId: UUID
) {

    companion object {
        fun makePaymentToMerchant(paymentAmount: BigDecimal, merchantId: UUID, orderId: UUID): Payment {

            if (paymentAmount <= BigDecimal.ZERO) throw PaymentValidationException("Payment amount must be greater than zero")

            return Payment(
                    id = UUID.randomUUID(),
                    paymentAmount = paymentAmount,
                    merchantId = merchantId,
                    orderId = orderId,
            )

            //TODO implement actual payment
        }
    }

}
