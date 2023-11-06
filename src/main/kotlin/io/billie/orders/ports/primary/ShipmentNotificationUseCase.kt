package io.billie.orders.ports.primary

import io.billie.common.infrastructure.DomainEventPublisher
import io.billie.orders.domain.Order
import io.billie.orders.domain.OrderShipmentException
import io.billie.orders.domain.event.OrderShippedEvent
import io.billie.orders.ports.secondary.OrderRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class ShipmentNotificationUseCase(
        private val orderRepository: OrderRepository,
        private val domainEventPublisher: DomainEventPublisher) {

    data class Input(val orderId: UUID, val shippedAmount: BigDecimal)

    fun execute(input: Input): Order {
        val order = orderRepository.find(input.orderId)
        order ?: throw OrderShipmentException("Cannot find order ${input.orderId}")
        order.notifyShipment(input.shippedAmount)
        val savedOrder = orderRepository.save(order)
        domainEventPublisher.publish(OrderShippedEvent(
                orderId = savedOrder.id,
                merchantId = savedOrder.merchantId,
                shippedAmount = input.shippedAmount)
        )
        return savedOrder
    }
}