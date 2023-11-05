package io.billie.orders.ports.primary

import io.billie.orders.domain.Order
import io.billie.orders.domain.OrderShipmentException
import io.billie.orders.ports.secondary.OrderRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class ShipmentNotificationUseCase(val orderRepository: OrderRepository) {

    data class Input(val orderId: UUID, val shippedAmount: BigDecimal)

    fun notifyShipment(input: Input): Order {
        val order = orderRepository.find(input.orderId)
        order ?: throw OrderShipmentException("Cannot find order ${input.orderId}")
        order.notifyShipment(input.shippedAmount)
        return orderRepository.save(order)
    }
}