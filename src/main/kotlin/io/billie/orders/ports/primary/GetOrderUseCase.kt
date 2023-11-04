package io.billie.orders.ports.primary

import io.billie.orders.ports.secondary.OrderRepository
import io.billie.orders.domain.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
class GetOrderUseCase(val orderRepository: OrderRepository) {

    data class Input(val orderId: UUID)

    fun getOrder(input: Input): Optional<Order> {
        return orderRepository.find(input.orderId)
    }
}