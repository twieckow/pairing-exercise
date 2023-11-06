package io.billie.orders.adapters.primary

import io.billie.orders.domain.event.OrderShippedEvent
import io.billie.payments.ports.primary.PayToMerchantUseCase
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class OrderShippedEventListener(private var payToMerchantUseCase: PayToMerchantUseCase) {

    @EventListener
    fun handleOrderShippedEvent(event: OrderShippedEvent) {
        payToMerchantUseCase.execute(PayToMerchantUseCase.Input(event.orderId, event.merchantId, event.shippedAmount))
    }
}
