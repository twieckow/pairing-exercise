package io.billie.orders.ports.primary

import io.billie.common.infrastructure.DomainEventPublisher
import io.billie.orders.domain.Order
import io.billie.orders.domain.event.OrderCreatedEvent
import io.billie.orders.ports.secondary.OrderRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class CreateOrderUseCase(
        private val orderRepository: OrderRepository,
        private val domainEventPublisher: DomainEventPublisher
) {

    data class Input(val orderAmount: BigDecimal, val merchantId: UUID)

    fun execute(input: Input): Order {
        val order = Order.initiate(input.orderAmount, input.merchantId)
        val savedOrder = orderRepository.save(order)
        domainEventPublisher.publish(OrderCreatedEvent(orderId = savedOrder.id))
        return savedOrder
    }
}